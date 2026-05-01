package com.example.mob_dev_portfolio.ui.recitation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.core.result.DomainResult
import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.data.repository.AyahRepository
import com.example.mob_dev_portfolio.data.repository.RecitationRepository
import com.example.mob_dev_portfolio.domain.model.RecitationResult
import com.example.mob_dev_portfolio.domain.recitation.AccuracyScorer
import com.example.mob_dev_portfolio.domain.recitation.ArabicNormaliser
import com.example.mob_dev_portfolio.domain.recitation.WordDiffer
import com.example.mob_dev_portfolio.recitation.NoSpeechDetectedException
import com.example.mob_dev_portfolio.recitation.RecogniserUnavailableException
import com.example.mob_dev_portfolio.recitation.SpeechRecogniser
import com.example.mob_dev_portfolio.recitation.SpeechRecogniserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the recitation sheet's state machine and the diff/score pipeline.
 * The fragment only handles permissions and rendering.
 *
 * The recogniser captures audio during the record phase, so `start` opens
 * the mic and `stop` finalises and returns the transcript.
 */
@HiltViewModel
class RecitationViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val ayahRepository: AyahRepository,
    private val recitationRepository: RecitationRepository,
    private val recogniserProvider: SpeechRecogniserProvider,
    private val clock: Clock,
) : ViewModel() {

    val surahNumber: Int = savedState["surahNumber"] ?: 1
    val ayahNumber: Int = savedState["ayahNumber"] ?: 1

    private var expectedText: String = ""
    private var testMode: Boolean = false
    private var startedAtMs: Long = 0L
    private var pollJob: Job? = null
    private var activeRecogniser: SpeechRecogniser? = null

    private val _state = MutableStateFlow<RecitationUiState>(
        RecitationUiState.Idle(expected = "", isTestMode = false),
    )
    val state: StateFlow<RecitationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val ayah = ayahRepository.findOne(surahNumber, ayahNumber)
            expectedText = ayah?.textArabic.orEmpty()
            _state.value = RecitationUiState.Idle(expectedText, testMode)
        }
    }

    fun setTestMode(enabled: Boolean) {
        testMode = enabled
        when (val current = _state.value) {
            is RecitationUiState.Idle -> _state.value = current.copy(isTestMode = enabled)
            is RecitationUiState.Recording -> _state.value = current.copy(isTestMode = enabled)
            else -> Unit
        }
    }

    /** Caller must have requested RECORD_AUDIO permission first. */
    fun startRecording() {
        viewModelScope.launch {
            val recogniser = recogniserProvider.current()
            if (!recogniser.hasPermission()) {
                _state.value = RecitationUiState.Error(expectedText, ErrorMessage.PERMISSION_DENIED)
                return@launch
            }
            runCatching { recogniser.start() }.fold(
                onSuccess = {
                    activeRecogniser = recogniser
                    startedAtMs = System.currentTimeMillis()
                    startPolling(recogniser)
                },
                onFailure = {
                    _state.value = RecitationUiState.Error(expectedText, ErrorMessage.GENERIC)
                },
            )
        }
    }

    fun stopRecording() {
        val recogniser = activeRecogniser ?: return
        pollJob?.cancel()
        _state.value = RecitationUiState.Processing(expectedText)
        viewModelScope.launch {
            when (val transcribed = recogniser.stop()) {
                is DomainResult.Success -> finalise(transcribed.data, recogniser.identifier)
                is DomainResult.Error -> emitError(transcribed.error)
                DomainResult.Loading -> Unit
            }
            activeRecogniser = null
        }
    }

    fun resetToIdle() {
        _state.value = RecitationUiState.Idle(expectedText, testMode)
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
        activeRecogniser?.cancel()
        activeRecogniser = null
    }

    private suspend fun finalise(transcript: String, recogniserId: String) {
        val expectedWords = ArabicNormaliser.words(expectedText).size
        val mistakes = WordDiffer.diff(expectedText, transcript)
        val score = AccuracyScorer.score(expectedWords, mistakes)
        val result = RecitationResult(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            recordedAt = clock.now(),
            transcript = transcript,
            mistakes = mistakes,
            accuracy = score,
            recogniser = recogniserId,
        )
        recitationRepository.save(result)
        _state.value = RecitationUiState.Result(expectedText, transcript, mistakes, score)
    }

    private fun emitError(error: Throwable) {
        val message = when (error) {
            is NoSpeechDetectedException -> ErrorMessage.NO_SPEECH_DETECTED
            is RecogniserUnavailableException -> ErrorMessage.RECOGNISER_UNAVAILABLE
            else -> ErrorMessage.GENERIC
        }
        _state.value = RecitationUiState.Error(expectedText, message)
    }

    private fun startPolling(recogniser: SpeechRecogniser) {
        pollJob = viewModelScope.launch {
            while (true) {
                _state.value = RecitationUiState.Recording(
                    expected = expectedText,
                    isTestMode = testMode,
                    elapsedMs = System.currentTimeMillis() - startedAtMs,
                    amplitude = recogniser.rms.value,
                )
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private companion object {
        const val POLL_INTERVAL_MS: Long = 60L
    }
}
