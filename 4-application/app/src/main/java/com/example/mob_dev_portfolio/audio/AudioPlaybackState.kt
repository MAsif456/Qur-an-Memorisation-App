package com.example.mob_dev_portfolio.audio

/** Snapshot of [AudioPlayer] state, exposed via Flow so UI doesn't depend on Media3. */
data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val surahNumber: Int? = null,
    val error: Throwable? = null,
) {
    companion object {
        val IDLE: AudioPlaybackState = AudioPlaybackState()
    }
}
