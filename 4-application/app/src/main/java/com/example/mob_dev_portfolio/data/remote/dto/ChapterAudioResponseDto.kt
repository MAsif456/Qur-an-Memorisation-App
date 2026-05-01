package com.example.mob_dev_portfolio.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Response from `GET /api/v4/chapter_recitations/{recitation_id}/{chapter_number}`. */
@JsonClass(generateAdapter = true)
data class ChapterAudioResponseDto(
    @Json(name = "audio_file") val audioFile: ChapterAudioDto?,
)

@JsonClass(generateAdapter = true)
data class ChapterAudioDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "chapter_id") val chapterId: Int?,
    @Json(name = "audio_url") val audioUrl: String?,
)
