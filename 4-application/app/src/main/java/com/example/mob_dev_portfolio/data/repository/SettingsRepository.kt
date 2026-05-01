package com.example.mob_dev_portfolio.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.mob_dev_portfolio.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/** Preferences DataStore-backed settings. Reads emit defaults until written. */
@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    fun observe(): Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            defaultReciterId = prefs[KEY_RECITER_ID] ?: UserSettings.DEFAULT_RECITER_ID,
            reminderTime = LocalTime.of(
                prefs[KEY_REMINDER_HOUR] ?: UserSettings.DEFAULT_REMINDER_HOUR,
                prefs[KEY_REMINDER_MINUTE] ?: 0,
            ),
            dailyGoal = prefs[KEY_DAILY_GOAL] ?: UserSettings.DEFAULT_DAILY_GOAL,
            darkModeFollowsSystem = prefs[KEY_DARK_MODE_FOLLOWS_SYSTEM] ?: true,
        )
    }

    suspend fun setReciter(id: Int) = dataStore.edit { it[KEY_RECITER_ID] = id }

    suspend fun setReminderTime(time: LocalTime) = dataStore.edit { prefs ->
        prefs[KEY_REMINDER_HOUR] = time.hour
        prefs[KEY_REMINDER_MINUTE] = time.minute
    }

    suspend fun setDailyGoal(goal: Int) = dataStore.edit { it[KEY_DAILY_GOAL] = goal.coerceAtLeast(1) }

    suspend fun setDarkModeFollowsSystem(value: Boolean) =
        dataStore.edit { it[KEY_DARK_MODE_FOLLOWS_SYSTEM] = value }

    private companion object {
        val KEY_RECITER_ID = intPreferencesKey("reciter_id")
        val KEY_REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val KEY_REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val KEY_DAILY_GOAL = intPreferencesKey("daily_goal")
        val KEY_DARK_MODE_FOLLOWS_SYSTEM = booleanPreferencesKey("dark_mode_follows_system")
    }
}
