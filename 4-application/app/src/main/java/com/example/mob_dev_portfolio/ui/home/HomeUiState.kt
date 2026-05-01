package com.example.mob_dev_portfolio.ui.home

import com.example.mob_dev_portfolio.domain.model.Recommendation

/** Home screen state. */
data class HomeUiState(
    val streakDays: Int = 0,
    val goalAchieved: Int = 0,
    val dailyGoal: Int = 0,
    val recommendations: List<Recommendation> = emptyList(),
    val isLoading: Boolean = true,
)
