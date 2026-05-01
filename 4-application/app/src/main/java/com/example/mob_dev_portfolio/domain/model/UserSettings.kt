package com.example.mob_dev_portfolio.domain.model

import java.time.LocalTime

/** Preferences persisted to DataStore. */
data class UserSettings(
    val defaultReciterId: Int,
    val reminderTime: LocalTime,
    val dailyGoal: Int,
    val darkModeFollowsSystem: Boolean,
) {
    companion object {
        val DEFAULT: UserSettings = UserSettings(
            defaultReciterId = DEFAULT_RECITER_ID,
            reminderTime = LocalTime.of(DEFAULT_REMINDER_HOUR, 0),
            dailyGoal = DEFAULT_DAILY_GOAL,
            darkModeFollowsSystem = true,
        )

        // Mishary Rashid Alafasy on Quran.com.
        const val DEFAULT_RECITER_ID: Int = 7
        const val DEFAULT_REMINDER_HOUR: Int = 19
        const val DEFAULT_DAILY_GOAL: Int = 5
    }
}
