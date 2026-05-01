package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity

/** Cache of audio URLs from the per-chapter recitation endpoint. */
@Entity(
    tableName = "chapter_audio",
    primaryKeys = ["reciterId", "surahNumber"],
)
data class ChapterAudioEntity(
    val reciterId: Int,
    val surahNumber: Int,
    val audioUrl: String,
)
