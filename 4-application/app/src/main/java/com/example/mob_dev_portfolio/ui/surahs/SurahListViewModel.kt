package com.example.mob_dev_portfolio.ui.surahs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.repository.SurahRepository
import com.example.mob_dev_portfolio.domain.model.Surah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Surah list filtered by the live search query. */
@HiltViewModel
class SurahListViewModel @Inject constructor(
    surahRepository: SurahRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val surahs: StateFlow<List<Surah>> = combine(
        surahRepository.observeAll(),
        _query,
    ) { items, q -> filter(items, q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }

    private fun filter(items: List<Surah>, q: String): List<Surah> {
        val trimmed = q.trim()
        if (trimmed.isEmpty()) return items
        val lower = trimmed.lowercase()
        return items.filter {
            it.number.toString() == trimmed ||
                it.nameTransliterated.lowercase().contains(lower) ||
                it.nameTranslation.lowercase().contains(lower) ||
                it.nameArabic.contains(trimmed)
        }
    }

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS: Long = 5_000L
    }
}
