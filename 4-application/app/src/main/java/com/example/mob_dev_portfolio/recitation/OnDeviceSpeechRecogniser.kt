package com.example.mob_dev_portfolio.recitation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import com.example.mob_dev_portfolio.core.result.DomainResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Streaming capture backed by [SpeechRecognizer] with `ar-SA`.
 *
 * The platform recogniser captures audio itself, so we listen during the
 * record phase and feed the waveform from [RecognitionListener.onRmsChanged].
 * `stop()` calls `stopListening()` and suspends until `onResults` (or
 * `onError`) fires.
 */
@Singleton
class OnDeviceSpeechRecogniser @Inject constructor(
    @ApplicationContext private val context: Context,
) : SpeechRecogniser {

    override val identifier: String = "on_device"

    private val _rms = MutableStateFlow(0)
    override val rms: StateFlow<Int> = _rms.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile private var recogniser: SpeechRecognizer? = null

    @Volatile private var pending: CompletableDeferred<DomainResult<String>>? = null

    override fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED

    override fun start() {
        require(hasPermission()) { "RECORD_AUDIO permission not granted" }
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            pending = CompletableDeferred(DomainResult.Error(RecogniserUnavailableException()))
            return
        }
        val deferred = CompletableDeferred<DomainResult<String>>()
        pending = deferred
        _rms.value = 0
        runOnMain { startOnMain(deferred) }
    }

    override suspend fun stop(): DomainResult<String> {
        val deferred = pending ?: return DomainResult.Error(RecogniserUnavailableException())
        runOnMain { recogniser?.stopListening() }
        return try {
            deferred.await()
        } finally {
            pending = null
            _rms.value = 0
        }
    }

    override fun cancel() {
        runOnMain {
            recogniser?.cancel()
            recogniser?.destroy()
            recogniser = null
        }
        pending?.takeIf { !it.isCompleted }
            ?.complete(DomainResult.Error(RecogniserUnavailableException()))
        pending = null
        _rms.value = 0
    }

    private fun startOnMain(deferred: CompletableDeferred<DomainResult<String>>) {
        val rec = SpeechRecognizer.createSpeechRecognizer(context)
        rec.setRecognitionListener(buildListener(deferred))
        recogniser = rec
        rec.startListening(buildIntent())
    }

    private fun buildIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, ARABIC_LOCALE)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, ARABIC_LOCALE)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
    }

    private fun buildListener(
        deferred: CompletableDeferred<DomainResult<String>>,
    ): RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onEndOfSpeech() = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit

        override fun onRmsChanged(rmsdB: Float) {
            // rmsdB is roughly -2..10 dB in practice; map to 0..100 with a clamp.
            val mapped = ((rmsdB + RMS_OFFSET_DB) / RMS_RANGE_DB * RMS_MAX).coerceIn(0f, RMS_MAX)
            _rms.value = mapped.toInt()
        }

        override fun onError(error: Int) {
            destroyAndComplete(deferred) {
                val cause = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                    -> NoSpeechDetectedException()
                    else -> RuntimeException("SpeechRecognizer error $error")
                }
                DomainResult.Error(cause)
            }
        }

        override fun onResults(results: Bundle?) {
            val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val transcript = list?.firstOrNull { it.isNotBlank() }.orEmpty()
            destroyAndComplete(deferred) {
                if (transcript.isBlank()) {
                    DomainResult.Error(NoSpeechDetectedException())
                } else {
                    DomainResult.Success(transcript)
                }
            }
        }
    }

    private fun destroyAndComplete(
        deferred: CompletableDeferred<DomainResult<String>>,
        result: () -> DomainResult<String>,
    ) {
        recogniser?.destroy()
        recogniser = null
        if (!deferred.isCompleted) deferred.complete(result())
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else mainHandler.post(block)
    }

    private companion object {
        const val ARABIC_LOCALE = "ar-SA"
        const val RMS_OFFSET_DB = 2f
        const val RMS_RANGE_DB = 12f
        const val RMS_MAX = 100f
    }
}
