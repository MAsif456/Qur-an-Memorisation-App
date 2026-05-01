package com.example.mob_dev_portfolio.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Response from `GET /api/v4/chapters`. */
@JsonClass(generateAdapter = true)
data class SurahListResponseDto(
    @Json(name = "chapters") val chapters: List<SurahDto>,
)

@JsonClass(generateAdapter = true)
data class SurahDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name_arabic") val nameArabic: String?,
    @Json(name = "name_simple") val nameSimple: String?,
    @Json(name = "translated_name") val translatedName: TranslatedNameDto?,
    @Json(name = "verses_count") val versesCount: Int?,
    @Json(name = "revelation_place") val revelationPlace: String?,
)

@JsonClass(generateAdapter = true)
data class TranslatedNameDto(
    @Json(name = "name") val name: String?,
)
