package com.example.mob_dev_portfolio.recitation

import com.example.mob_dev_portfolio.core.result.DomainResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Streaming capture API. The on-device recogniser captures audio internally
 * and cannot accept a pre-recorded file, so the contract is start / stop
 * rather than transcribe(file).
 *
 *  - [start]: open the mic and begin capturing. Caller must hold RECORD_AUDIO.
 *  - [rms]: 0..100 amplitude samples driven by the active capture, for the
 *    waveform visualiser.
 *  - [stop]: finalise and return the transcript. Suspends until the recogniser
 *    delivers a final result.
 *  - [cancel]: abort without producing a transcript.
 *
 * Implementations are single-session: a [start] must be paired with [stop]
 * or [cancel] before [start] is called again.
 */
interface SpeechRecogniser {
    val identifier: String
    val rms: StateFlow<Int>

    fun hasPermission(): Boolean
    fun start()
    suspend fun stop(): DomainResult<String>
    fun cancel()
}

class NoSpeechDetectedException : RuntimeException("No speech detected")

class RecogniserUnavailableException : RuntimeException("Speech recognition unavailable")
