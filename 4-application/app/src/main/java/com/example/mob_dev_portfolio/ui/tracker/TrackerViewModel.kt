package com.example.mob_dev_portfolio.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.repository.MemorisationRepository
import com.example.mob_dev_portfolio.data.repository.RevisionRepository
import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.domain.model.RevisionTask
import com.example.mob_dev_portfolio.domain.recommendations.MemorisationRecommender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Tracker tabs: memorised, due, weak. Shared across pages via the parent fragment. */
@HiltViewModel
class TrackerViewModel @Inject constructor(
    memorisationRepository: MemorisationRepository,
    revisionRepository: RevisionRepository,
) : ViewModel() {

    val memorised: StateFlow<List<AyahProgress>> = memorisationRepository.observeAll()
        .map { rows -> rows.filter { it.status != MemorisationStatus.NEW } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), emptyList())

    val due: StateFlow<List<RevisionTask>> = revisionRepository.observeDueNow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), emptyList())

    val weak: StateFlow<List<AyahProgress>> = memorisationRepository.observeWeak(
        MemorisationRecommender.DEFAULT_WEAK_THRESHOLD,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), emptyList())

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS: Long = 5_000L
    }
}
