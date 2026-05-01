package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import com.example.mob_dev_portfolio.data.remote.dto.SurahDto

/**
 * DTO-to-entity mappers for Surah metadata.
 *
 * Defensive about partial payloads: missing fields fall back to safe values
 * so a malformed entry can't poison the cache.
 */
object SurahMapper {

    private const val UNKNOWN_TEXT: String = ""
    private const val UNKNOWN_PLACE: String = "unknown"
    private const val MIN_SURAH: Int = 1
    private const val MAX_SURAH: Int = 114

    fun toEntity(dto: SurahDto): SurahEntity = SurahEntity(
        number = dto.id,
        nameArabic = dto.nameArabic.orFallback(UNKNOWN_TEXT),
        nameTransliterated = dto.nameSimple.orFallback(UNKNOWN_TEXT),
        nameTranslation = dto.translatedName?.name.orFallback(UNKNOWN_TEXT),
        ayahCount = (dto.versesCount ?: 0).coerceAtLeast(0),
        revelationPlace = dto.revelationPlace.orFallback(UNKNOWN_PLACE),
    )

    /** Drops entries whose id is outside 1..114 so a bad row can't alias a real Surah. */
    fun toEntities(dtos: List<SurahDto>): List<SurahEntity> = dtos
        .filter { it.id in MIN_SURAH..MAX_SURAH }
        .map(::toEntity)

    private fun String?.orFallback(fallback: String): String =
        if (this.isNullOrBlank()) fallback else this
}
