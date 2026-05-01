package com.example.mob_dev_portfolio.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Response from `GET /api/v4/verses/by_chapter/{id}`. */
@JsonClass(generateAdapter = true)
data class AyahResponseDto(
    @Json(name = "verses") val verses: List<AyahDto>,
)

@JsonClass(generateAdapter = true)
data class AyahDto(
    @Json(name = "verse_key") val verseKey: String?,
    @Json(name = "text_uthmani") val textUthmani: String?,
    @Json(name = "translations") val translations: List<TranslationDto>? = null,
)

@JsonClass(generateAdapter = true)
data class TranslationDto(
    @Json(name = "text") val text: String?,
)
