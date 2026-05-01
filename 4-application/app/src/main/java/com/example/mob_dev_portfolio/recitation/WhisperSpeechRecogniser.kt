package com.example.mob_dev_portfolio.recitation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.mob_dev_portfolio.BuildConfig
import com.example.mob_dev_portfolio.core.result.DomainResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional cloud capture path. Records to an M4A file with [MediaRecorder]
 * and uploads to a Whisper-compatible endpoint on stop.
 *
 * Not used by any shipped build: `BuildConfig.WHISPER_BASE_URL` defaults to
 * "" so [SpeechRecogniserProvider] always picks the on-device recogniser.
 * The class exists to demonstrate that the on-device path can be swapped
 * without touching calling code.
 */
@Singleton
class WhisperSpeechRecogniser @Inject constructor(
    @ApplicationContext private val context: Context,
) : SpeechRecogniser {

    override val identifier: String = "whisper"

    private val _rms = MutableStateFlow(0)
    override val rms: StateFlow<Int> = _rms.asStateFlow()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    private val mediaType = "audio/mp4".toMediaType()
    private val captureScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var pollJob: Job? = null

    override fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED

    /** True if `{base}/health` returns 2xx within the timeout. */
    fun healthCheck(baseUrl: String): Boolean {
        if (baseUrl.isBlank()) return false
        val request = Request.Builder()
            .url(baseUrl.trimEnd('/') + "/health")
            .build()
        return runCatching { client.newCall(request).execute().use { it.isSuccessful } }
            .getOrDefault(false)
    }

    override fun start() {
        require(hasPermission()) { "RECORD_AUDIO permission not granted" }
        _rms.value = 0
        val file = File(context.cacheDir, OUTPUT_FILENAME)
        if (file.exists()) file.delete()
        val rec = newRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(SAMPLE_RATE_HZ)
            setAudioEncodingBitRate(BIT_RATE)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorder = rec
        outputFile = file
        startPolling()
    }

    override suspend fun stop(): DomainResult<String> = withContext(Dispatchers.IO) {
        pollJob?.cancel()
        val rec = recorder
        val file = outputFile
        recorder = null
        outputFile = null
        _rms.value = 0
        if (rec == null || file == null) {
            return@withContext DomainResult.Error(RecogniserUnavailableException())
        }
        runCatching { rec.stop() }
        rec.release()
        upload(file, BuildConfig.WHISPER_BASE_URL)
    }

    override fun cancel() {
        pollJob?.cancel()
        recorder?.runCatching { stop() }
        recorder?.release()
        recorder = null
        outputFile?.delete()
        outputFile = null
        _rms.value = 0
    }

    private fun startPolling() {
        pollJob = captureScope.launch {
            while (isActive) {
                val raw = recorder?.runCatching { maxAmplitude }?.getOrNull() ?: 0
                _rms.value = (raw / MAX_AMPLITUDE_DIV).coerceIn(0, RMS_MAX)
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun newRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()

    private suspend fun upload(file: File, baseUrl: String): DomainResult<String> =
        withContext(Dispatchers.IO) {
            if (baseUrl.isBlank()) {
                return@withContext DomainResult.Error(RecogniserUnavailableException())
            }
            runCatching {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "audio",
                        file.name,
                        file.asRequestBody(mediaType),
                    )
                    .build()
                val request = Request.Builder()
                    .url(baseUrl.trimEnd('/') + "/transcribe")
                    .post(body)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) error("HTTP ${response.code}")
                    val json = JSONObject(response.body?.string().orEmpty())
                    val text = json.optString("text").trim()
                    if (text.isEmpty()) throw NoSpeechDetectedException()
                    text
                }
            }.fold(
                onSuccess = { DomainResult.Success(it) },
                onFailure = { DomainResult.Error(it) },
            )
        }

    private companion object {
        const val OUTPUT_FILENAME = "recitation.m4a"
        const val SAMPLE_RATE_HZ = 44_100
        const val BIT_RATE = 96_000
        const val POLL_INTERVAL_MS: Long = 60L
        const val MAX_AMPLITUDE_DIV = 327
        const val RMS_MAX = 100
        const val TIMEOUT_SECONDS = 5L
    }
}
