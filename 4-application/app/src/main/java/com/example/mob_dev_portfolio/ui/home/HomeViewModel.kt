package com.example.mob_dev_portfolio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.data.repository.MemorisationRepository
import com.example.mob_dev_portfolio.data.repository.SettingsRepository
import com.example.mob_dev_portfolio.domain.recommendations.MemorisationRecommender
import com.example.mob_dev_portfolio.domain.streaks.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId
import javax.inject.Inject

/** Combines streak, goal progress, and recommendations into [HomeUiState]. */
@HiltViewModel
class HomeViewModel @Inject constructor(
    memorisationRepository: MemorisationRepository,
    settingsRepository: SettingsRepository,
    recommender: MemorisationRecommender,
    private val clock: Clock,
) : ViewModel() {

    val state: StateFlow<HomeUiState> = combine(
        memorisationRepository.observeStreak(),
        memorisationRepository.observeAll(),
        memorisationRepository.observeTouchedCount(),
        settingsRepository.observe(),
    ) { streak, progress, touched, settings ->
        val today = clock.now().atZone(ZoneId.systemDefault()).toLocalDate()
        val effectiveStreak = streak?.let { StreakCalculator.effective(it, today) } ?: 0
        HomeUiState(
            streakDays = effectiveStreak,
            goalAchieved = touched,
            dailyGoal = settings.dailyGoal,
            recommendations = recommender.recommend(
                progress = progress,
                newSuggestions = listOf(1 to 1),
                now = clock.now(),
            ),
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = HomeUiState(),
    )

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS: Long = 5_000L
    }
}
