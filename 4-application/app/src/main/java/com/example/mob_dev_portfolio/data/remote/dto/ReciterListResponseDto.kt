package com.example.mob_dev_portfolio.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Response from `GET /api/v4/resources/recitations`. */
@JsonClass(generateAdapter = true)
data class ReciterListResponseDto(
    @Json(name = "recitations") val recitations: List<ReciterDto>,
)

@JsonClass(generateAdapter = true)
data class ReciterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "reciter_name") val reciterName: String?,
    @Json(name = "style") val style: String?,
)
