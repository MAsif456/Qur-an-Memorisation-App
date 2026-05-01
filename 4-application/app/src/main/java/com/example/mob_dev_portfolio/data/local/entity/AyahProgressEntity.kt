package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus

/**
 * Per-ayah memorisation and SM-2 schedule. A row exists only once the ayah
 * has been touched; ayahs without a row are implicitly [MemorisationStatus.NEW].
 *
 * `nextReviewAt` is indexed so the "due today" query stays cheap.
 */
@Entity(
    tableName = "ayah_progress",
    primaryKeys = ["surahNumber", "ayahNumber"],
    indices = [Index("nextReviewAt"), Index("status")],
)
data class AyahProgressEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val status: MemorisationStatus,
    val repetitions: Int,
    val easinessFactor: Double,
    val intervalDays: Int,
    val lastReviewedAt: Long?,
    val nextReviewAt: Long?,
    val lastAccuracy: Int?,
)
