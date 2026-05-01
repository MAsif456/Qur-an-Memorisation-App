package com.example.mob_dev_portfolio.data.seed

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mob_dev_portfolio.data.local.AppDatabase
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Bundled seed parses, populates the cache, and is idempotent. */
@RunWith(RobolectricTestRunner::class)
class SeedLoaderTest {

    private lateinit var db: AppDatabase
    private lateinit var loader: SeedLoader

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        loader = SeedLoader(
            context = context,
            surahDao = db.surahDao(),
            ayahDao = db.ayahDao(),
            reciterDao = db.reciterDao(),
            chapterAudioDao = db.chapterAudioDao(),
            moshi = moshi,
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seed populates the surah catalogue`() = runTest {
        loader.seedIfRequired()
        val surahs = db.surahDao().observeAll().first()
        assertThat(surahs).hasSize(114)
        assertThat(surahs.first().number).isEqualTo(1)
        assertThat(surahs.last().number).isEqualTo(114)
    }

    @Test
    fun `seed bundles full Al-Fatiha ayahs`() = runTest {
        loader.seedIfRequired()
        val ayahs = db.ayahDao().observeBySurah(1).first()
        assertThat(ayahs).hasSize(7)
        assertThat(ayahs.first().textArabic).isNotEmpty()
    }

    @Test
    fun `running seed twice is idempotent`() = runTest {
        loader.seedIfRequired()
        loader.seedIfRequired()
        assertThat(db.surahDao().count()).isEqualTo(114)
    }

    @Test
    fun `chapter audio rows present for bundled reciter`() = runTest {
        loader.seedIfRequired()
        val audio = db.chapterAudioDao().find(reciterId = 7, surahNumber = 1)
        assertThat(audio).isNotNull()
        assertThat(audio!!.audioUrl).startsWith("https://")
    }
}
