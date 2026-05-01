package com.example.mob_dev_portfolio.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import com.example.mob_dev_portfolio.data.local.entity.AyahProgressEntity
import com.example.mob_dev_portfolio.data.local.entity.RecitationAttemptEntity
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** In-memory DAO exercises. Real migration tests will land with the first v2 schema. */
@RunWith(RobolectricTestRunner::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `surah upsert and observe roundtrip`() = runTest {
        val surah = SurahEntity(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "makkah")
        db.surahDao().upsert(surah)
        assertThat(db.surahDao().observeAll().first()).containsExactly(surah)
        assertThat(db.surahDao().count()).isEqualTo(1)
    }

    @Test
    fun `ayah lookup by composite key`() = runTest {
        db.surahDao().upsert(SurahEntity(1, "x", "x", "x", 7, "makkah"))
        val ayah = AyahEntity(1, 1, "بسم", "In the name")
        db.ayahDao().upsert(ayah)
        assertThat(db.ayahDao().findOne(1, 1)).isEqualTo(ayah)
        assertThat(db.ayahDao().countInSurah(1)).isEqualTo(1)
    }

    @Test
    fun `progress observation reacts to upsert`() = runTest {
        db.surahDao().upsert(SurahEntity(1, "x", "x", "x", 7, "makkah"))
        db.memorisationDao().upsert(
            AyahProgressEntity(1, 1, MemorisationStatus.LEARNING, 0, 2.5, 1, null, null, null),
        )
        val rows = db.memorisationDao().observeAll().first()
        assertThat(rows).hasSize(1)
        assertThat(rows.single().status).isEqualTo(MemorisationStatus.LEARNING)
    }

    @Test
    fun `recitation insert returns generated id`() = runTest {
        db.surahDao().upsert(SurahEntity(1, "x", "x", "x", 7, "makkah"))
        val id = db.recitationDao().insert(
            RecitationAttemptEntity(0, 1, 1, 100L, 100, 0, "بسم", "on_device"),
        )
        assertThat(id).isGreaterThan(0L)
    }

    @Test
    fun `weak ayahs filtered by accuracy threshold`() = runTest {
        db.surahDao().upsert(SurahEntity(1, "x", "x", "x", 7, "makkah"))
        db.memorisationDao().upsert(
            AyahProgressEntity(1, 1, MemorisationStatus.REVIEWING, 1, 2.5, 1, null, null, 50),
        )
        db.memorisationDao().upsert(
            AyahProgressEntity(1, 2, MemorisationStatus.REVIEWING, 1, 2.5, 1, null, null, 90),
        )
        val weak = db.memorisationDao().observeWeakAyahs(70).first()
        assertThat(weak.map { it.ayahNumber }).containsExactly(1)
    }
}
