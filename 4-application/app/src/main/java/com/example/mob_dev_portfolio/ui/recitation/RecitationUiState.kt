package com.example.mob_dev_portfolio.ui.recitation

import com.example.mob_dev_portfolio.domain.model.AccuracyScore
import com.example.mob_dev_portfolio.domain.model.Mistake

/**
 * State machine for the recitation sheet:
 * Idle -> Recording -> Processing -> Result (or Error).
 */
sealed interface RecitationUiState {

    data class Idle(
        val expected: String,
        val isTestMode: Boolean,
    ) : RecitationUiState

    data class Recording(
        val expected: String,
        val isTestMode: Boolean,
        val elapsedMs: Long,
        val amplitude: Int,
    ) : RecitationUiState

    data class Processing(val expected: String) : RecitationUiState

    data class Result(
        val expected: String,
        val transcript: String,
        val mistakes: List<Mistake>,
        val score: AccuracyScore,
    ) : RecitationUiState

    data class Error(
        val expected: String,
        val message: ErrorMessage,
    ) : RecitationUiState
}

enum class ErrorMessage {
    PERMISSION_DENIED,
    RECOGNISER_UNAVAILABLE,
    NO_SPEECH_DETECTED,
    GENERIC,
}
