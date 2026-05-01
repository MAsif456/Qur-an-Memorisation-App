package com.example.mob_dev_portfolio.ui.surahs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.audio.AudioPlaybackState
import com.example.mob_dev_portfolio.audio.AudioPlayer
import com.example.mob_dev_portfolio.data.repository.AyahRepository
import com.example.mob_dev_portfolio.data.repository.MemorisationRepository
import com.example.mob_dev_portfolio.data.repository.SettingsRepository
import com.example.mob_dev_portfolio.data.repository.SurahRepository
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.domain.model.Surah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Combines the Surah, its ayahs, and progress into [AyahRow]s. First view
 * triggers a network refresh so the cache fills lazily.
 */
@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    savedState: SavedStateHandle,
    surahRepository: SurahRepository,
    private val ayahRepository: AyahRepository,
    private val memorisationRepository: MemorisationRepository,
    private val settingsRepository: SettingsRepository,
    private val audioPlayer: AudioPlayer,
) : ViewModel() {

    val surahNumber: Int = savedState["surahNumber"] ?: 1

    val surah: StateFlow<Surah?> = surahRepository.observeAll()
        .map { list -> list.firstOrNull { it.number == surahNumber } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), null)

    val rows: StateFlow<List<AyahRow>> = combine(
        ayahRepository.observeBySurah(surahNumber),
        memorisationRepository.observeAll(),
    ) { ayahs, progress ->
        val statusBy = progress.associateBy { it.surahNumber to it.ayahNumber }
        ayahs.map { ayah ->
            val status = statusBy[ayah.surahNumber to ayah.ayahNumber]?.status ?: MemorisationStatus.NEW
            AyahRow(ayah, status)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), emptyList())

    val playback: StateFlow<AudioPlaybackState> = audioPlayer.state

    init {
        viewModelScope.launch {
            ayahRepository.refreshSurah(surahNumber)
        }
    }

    fun toggleMemorised(row: AyahRow) {
        viewModelScope.launch {
            if (row.status == MemorisationStatus.NEW) {
                memorisationRepository.markMemorised(row.ayah.surahNumber, row.ayah.ayahNumber)
            } else {
                memorisationRepository.unmark(row.ayah.surahNumber, row.ayah.ayahNumber)
            }
        }
    }

    fun playSurah() {
        viewModelScope.launch {
            val reciterId = settingsRepository.observe().first().defaultReciterId
            val url = ayahRepository.audioUrl(reciterId, surahNumber) ?: return@launch
            audioPlayer.play(surahNumber, url)
        }
    }

    fun pause() = audioPlayer.pause()

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS: Long = 5_000L
    }
}
