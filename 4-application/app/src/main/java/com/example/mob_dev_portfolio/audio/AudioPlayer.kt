package com.example.mob_dev_portfolio.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin Media3 [ExoPlayer] wrapper exposing playback as a [StateFlow] so
 * ViewModels never depend on Media3 directly.
 *
 * The player is created lazily and kept alive for the process lifetime so
 * the second and subsequent plays are warm. [release] is provided for
 * tests; production code doesn't need to call it.
 */
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val _state = MutableStateFlow(AudioPlaybackState.IDLE)
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private var player: ExoPlayer? = null
    private var loadedSurah: Int? = null

    private fun ensurePlayer(): ExoPlayer {
        val existing = player
        if (existing != null) return existing
        val created = ExoPlayer.Builder(context).build().apply {
            addListener(listener)
        }
        player = created
        return created
    }

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) = refreshState()
        override fun onPlaybackStateChanged(playbackState: Int) = refreshState()
        override fun onPlayerError(error: PlaybackException) {
            _state.value = _state.value.copy(error = error, isPlaying = false)
        }
    }

    /** Resumes the existing media item if the same surah is already loaded. */
    fun play(surahNumber: Int, audioUrl: String) {
        val p = ensurePlayer()
        if (loadedSurah != surahNumber) {
            p.setMediaItem(MediaItem.fromUri(audioUrl))
            p.prepare()
            loadedSurah = surahNumber
        }
        _state.value = _state.value.copy(error = null, surahNumber = surahNumber)
        p.playWhenReady = true
    }

    fun pause() {
        player?.playWhenReady = false
    }

    fun stop() {
        player?.run {
            playWhenReady = false
            seekTo(0L)
        }
    }

    fun release() {
        player?.run {
            removeListener(listener)
            release()
        }
        player = null
        loadedSurah = null
        _state.value = AudioPlaybackState.IDLE
    }

    private fun refreshState() {
        val p = player ?: return
        _state.value = AudioPlaybackState(
            isPlaying = p.isPlaying,
            isBuffering = p.playbackState == Player.STATE_BUFFERING,
            positionMs = p.currentPosition.coerceAtLeast(0L),
            durationMs = p.duration.coerceAtLeast(0L),
            surahNumber = loadedSurah,
            error = _state.value.error,
        )
    }
}
