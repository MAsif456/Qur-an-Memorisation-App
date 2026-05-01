package com.example.mob_dev_portfolio.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.repository.ReciterRepository
import com.example.mob_dev_portfolio.data.repository.SettingsRepository
import com.example.mob_dev_portfolio.domain.model.Reciter
import com.example.mob_dev_portfolio.domain.model.UserSettings
import com.example.mob_dev_portfolio.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/** Settings screen state. */
data class SettingsUiState(
    val settings: UserSettings = UserSettings.DEFAULT,
    val reciters: List<Reciter> = emptyList(),
)

/**
 * Combines preferences and the reciter cache. Dark mode also pokes
 * [AppCompatDelegate] so the change applies without restarting the process.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reciterRepository: ReciterRepository,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.observe(),
        reciterRepository.observeAll(),
    ) { settings, reciters -> SettingsUiState(settings, reciters) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), SettingsUiState())

    init {
        viewModelScope.launch { reciterRepository.refresh() }
    }

    fun setReciter(id: Int) = viewModelScope.launch { settingsRepository.setReciter(id) }

    fun setReminderTime(time: LocalTime) = viewModelScope.launch {
        settingsRepository.setReminderTime(time)
        notificationScheduler.schedule(time)
    }

    fun setGoal(goal: Int) = viewModelScope.launch { settingsRepository.setDailyGoal(goal) }

    /** Fires the reminder worker now so the user can confirm notifications work. */
    fun sendTestNotification() = notificationScheduler.runNow()

    fun setDarkModeFollowsSystem(value: Boolean) = viewModelScope.launch {
        settingsRepository.setDarkModeFollowsSystem(value)
        AppCompatDelegate.setDefaultNightMode(
            if (value) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            },
        )
    }

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS: Long = 5_000L
    }
}
