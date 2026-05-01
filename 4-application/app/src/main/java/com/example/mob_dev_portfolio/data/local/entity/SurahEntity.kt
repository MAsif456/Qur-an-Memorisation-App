package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached Surah metadata. Seeded on first launch and refreshed from the API. */
@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val nameArabic: String,
    val nameTransliterated: String,
    val nameTranslation: String,
    val ayahCount: Int,
    val revelationPlace: String,
)
