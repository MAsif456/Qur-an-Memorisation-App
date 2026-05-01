package com.example.mob_dev_portfolio.data.seed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Schema of `assets/surahs_seed.json`. Bundles enough data to make the app
 * usable on a clean install with no network: Surah catalogue, default
 * reciter list, and one short Surah's ayahs and audio.
 */
@JsonClass(generateAdapter = true)
data class SeedDto(
    @Json(name = "surahs") val surahs: List<SeedSurahDto>,
    @Json(name = "reciters") val reciters: List<SeedReciterDto>,
    @Json(name = "ayahs") val ayahs: List<SeedAyahDto>,
    @Json(name = "chapter_audios") val chapterAudios: List<SeedChapterAudioDto>,
)

@JsonClass(generateAdapter = true)
data class SeedSurahDto(
    val number: Int,
    val nameArabic: String,
    val nameTransliterated: String,
    val nameTranslation: String,
    val ayahCount: Int,
    val revelationPlace: String,
)

@JsonClass(generateAdapter = true)
data class SeedReciterDto(
    val id: Int,
    val name: String,
    val style: String?,
)

@JsonClass(generateAdapter = true)
data class SeedAyahDto(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textTranslation: String?,
)

@JsonClass(generateAdapter = true)
data class SeedChapterAudioDto(
    val reciterId: Int,
    val surahNumber: Int,
    val audioUrl: String,
)
