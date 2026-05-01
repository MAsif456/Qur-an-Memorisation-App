package com.example.mob_dev_portfolio.recitation

import com.example.mob_dev_portfolio.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Picks the recogniser for the current process.
 *
 * `BuildConfig.WHISPER_BASE_URL` blank (the committed default) selects the
 * on-device recogniser. Otherwise a one-shot health check decides; on any
 * failure we fall back to on-device silently. The result is cached so the
 * health check runs once per process.
 */
@Singleton
class SpeechRecogniserProvider @Inject constructor(
    private val onDevice: OnDeviceSpeechRecogniser,
    private val whisper: WhisperSpeechRecogniser,
) {

    @Volatile private var cachedSelection: SpeechRecogniser? = null

    suspend fun current(): SpeechRecogniser {
        cachedSelection?.let { return it }
        return withContext(Dispatchers.IO) {
            val resolved = resolve()
            cachedSelection = resolved
            resolved
        }
    }

    private fun resolve(): SpeechRecogniser {
        val configured = BuildConfig.WHISPER_BASE_URL
        if (configured.isBlank()) return onDevice
        return if (whisper.healthCheck(configured)) whisper else onDevice
    }
}
