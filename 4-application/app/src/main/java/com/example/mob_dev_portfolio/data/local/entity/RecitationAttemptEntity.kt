package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Append-only log of recitation attempts. */
@Entity(
    tableName = "recitation_attempts",
    indices = [Index(value = ["surahNumber", "ayahNumber", "recordedAt"])],
)
data class RecitationAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val surahNumber: Int,
    val ayahNumber: Int,
    val recordedAt: Long,
    val accuracy: Int,
    val mistakeCount: Int,
    val transcript: String,
    val recogniser: String,
)
