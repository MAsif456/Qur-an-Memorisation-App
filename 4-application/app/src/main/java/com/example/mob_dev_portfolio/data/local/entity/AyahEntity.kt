package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cached ayah text. The composite primary key (surahNumber, ayahNumber) is
 * the natural key everywhere else in the app.
 */
@Entity(
    tableName = "ayahs",
    primaryKeys = ["surahNumber", "ayahNumber"],
    foreignKeys = [
        ForeignKey(
            entity = SurahEntity::class,
            parentColumns = ["number"],
            childColumns = ["surahNumber"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("surahNumber")],
)
data class AyahEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textTranslation: String?,
)
