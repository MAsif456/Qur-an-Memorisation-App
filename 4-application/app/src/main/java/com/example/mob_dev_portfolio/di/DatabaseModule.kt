package com.example.mob_dev_portfolio.di

import android.content.Context
import androidx.room.Room
import com.example.mob_dev_portfolio.data.local.AppDatabase
import com.example.mob_dev_portfolio.data.local.dao.AyahDao
import com.example.mob_dev_portfolio.data.local.dao.ChapterAudioDao
import com.example.mob_dev_portfolio.data.local.dao.MemorisationDao
import com.example.mob_dev_portfolio.data.local.dao.RecitationDao
import com.example.mob_dev_portfolio.data.local.dao.ReciterDao
import com.example.mob_dev_portfolio.data.local.dao.RevisionDao
import com.example.mob_dev_portfolio.data.local.dao.StreakDao
import com.example.mob_dev_portfolio.data.local.dao.SurahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the database and exposes each DAO directly. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideSurahDao(db: AppDatabase): SurahDao = db.surahDao()

    @Provides
    fun provideAyahDao(db: AppDatabase): AyahDao = db.ayahDao()

    @Provides
    fun provideMemorisationDao(db: AppDatabase): MemorisationDao = db.memorisationDao()

    @Provides
    fun provideRevisionDao(db: AppDatabase): RevisionDao = db.revisionDao()

    @Provides
    fun provideRecitationDao(db: AppDatabase): RecitationDao = db.recitationDao()

    @Provides
    fun provideStreakDao(db: AppDatabase): StreakDao = db.streakDao()

    @Provides
    fun provideReciterDao(db: AppDatabase): ReciterDao = db.reciterDao()

    @Provides
    fun provideChapterAudioDao(db: AppDatabase): ChapterAudioDao = db.chapterAudioDao()
}
