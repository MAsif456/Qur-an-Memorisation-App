package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import com.example.mob_dev_portfolio.data.remote.dto.AyahDto

/** DTO-to-entity mappers for ayahs. Skips rows whose verse_key is unparseable. */
object AyahMapper {

    private const val MAX_SURAH: Int = 114

    /** Parse "surah:ayah" or return null if the input is missing or malformed. */
    fun parseVerseKey(verseKey: String?): Pair<Int, Int>? {
        if (verseKey.isNullOrBlank()) return null
        val parts = verseKey.split(':')
        if (parts.size != 2) return null
        val surah = parts[0].toIntOrNull() ?: return null
        val ayah = parts[1].toIntOrNull() ?: return null
        if (surah !in 1..MAX_SURAH || ayah <= 0) return null
        return surah to ayah
    }

    fun toEntity(dto: AyahDto): AyahEntity? {
        val (surah, ayah) = parseVerseKey(dto.verseKey) ?: return null
        val arabic = dto.textUthmani.orEmpty()
        val translation = dto.translations
            ?.asSequence()
            ?.mapNotNull { it.text }
            ?.firstOrNull { it.isNotBlank() }
        return AyahEntity(
            surahNumber = surah,
            ayahNumber = ayah,
            textArabic = arabic,
            textTranslation = translation,
        )
    }

    fun toEntities(dtos: List<AyahDto>): List<AyahEntity> =
        dtos.mapNotNull(::toEntity)
}
