package com.example.mob_dev_portfolio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.local.converter.InstantConverter
import com.example.mob_dev_portfolio.data.local.dao.AyahDao
import com.example.mob_dev_portfolio.data.local.dao.ChapterAudioDao
import com.example.mob_dev_portfolio.data.local.dao.MemorisationDao
import com.example.mob_dev_portfolio.data.local.dao.RecitationDao
import com.example.mob_dev_portfolio.data.local.dao.ReciterDao
import com.example.mob_dev_portfolio.data.local.dao.RevisionDao
import com.example.mob_dev_portfolio.data.local.dao.StreakDao
import com.example.mob_dev_portfolio.data.local.dao.SurahDao
import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import com.example.mob_dev_portfolio.data.local.entity.AyahProgressEntity
import com.example.mob_dev_portfolio.data.local.entity.ChapterAudioEntity
import com.example.mob_dev_portfolio.data.local.entity.RecitationAttemptEntity
import com.example.mob_dev_portfolio.data.local.entity.ReciterEntity
import com.example.mob_dev_portfolio.data.local.entity.StreakEntity
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity

/**
 * Single Room database hosting every entity.
 *
 * Started at version 1; future schema changes need explicit migrations.
 * Destructive fallback is only enabled in tests via the in-memory builder.
 */
@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        AyahProgressEntity::class,
        RecitationAttemptEntity::class,
        ReciterEntity::class,
        StreakEntity::class,
        ChapterAudioEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun memorisationDao(): MemorisationDao
    abstract fun revisionDao(): RevisionDao
    abstract fun recitationDao(): RecitationDao
    abstract fun streakDao(): StreakDao
    abstract fun reciterDao(): ReciterDao
    abstract fun chapterAudioDao(): ChapterAudioDao

    companion object {
        const val DATABASE_NAME: String = "quran_memoriser.db"
    }
}
