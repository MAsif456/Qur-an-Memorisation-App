package com.example.mob_dev_portfolio.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.core.time.SystemClock
import com.example.mob_dev_portfolio.domain.recommendations.MemorisationRecommender
import com.example.mob_dev_portfolio.domain.revision.SpacedRepetitionScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** App-scoped singletons that don't fit in the database or network module. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DATASTORE_NAME = "user_settings"
    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideScheduler(): SpacedRepetitionScheduler = SpacedRepetitionScheduler()

    @Provides
    @Singleton
    fun provideRecommender(): MemorisationRecommender = MemorisationRecommender()
}
