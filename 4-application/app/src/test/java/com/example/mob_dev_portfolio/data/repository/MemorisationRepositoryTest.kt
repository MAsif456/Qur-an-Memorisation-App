package com.example.mob_dev_portfolio.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mob_dev_portfolio.data.local.AppDatabase
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.testutil.FakeClock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(RobolectricTestRunner::class)
class MemorisationRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var clock: FakeClock
    private lateinit var repository: MemorisationRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        // Pre-seed a surah row so foreign-key constraints succeed if Room ever enforces them.
        val seedSurah = SurahEntity(1, "x", "x", "x", 7, "makkah")
        kotlinx.coroutines.runBlocking { db.surahDao().upsert(seedSurah) }
        clock = FakeClock()
        repository = MemorisationRepository(db.memorisationDao(), db.streakDao(), clock)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `markMemorised inserts a learning row when none exists`() = runTest {
        repository.markMemorised(1, 1)
        val row = repository.findOne(1, 1)
        assertThat(row).isNotNull()
        assertThat(row!!.status).isEqualTo(MemorisationStatus.LEARNING)
    }

    @Test
    fun `markMemorised is idempotent for already-touched ayahs`() = runTest {
        repository.markMemorised(1, 1)
        repository.markMemorised(1, 1)
        val rows = repository.observeAll().first()
        assertThat(rows).hasSize(1)
    }

    @Test
    fun `unmark deletes the row`() = runTest {
        repository.markMemorised(1, 1)
        repository.unmark(1, 1)
        assertThat(repository.findOne(1, 1)).isNull()
    }

    @Test
    fun `recordActivity sets streak to one on first call`() = runTest {
        repository.recordActivity()
        val streak = repository.observeStreak().first()
        assertThat(streak).isNotNull()
        assertThat(streak!!.current).isEqualTo(1)
    }

    @Test
    fun `recordActivity increments streak on consecutive days`() = runTest {
        repository.recordActivity()
        clock.now = clock.now.plus(1, ChronoUnit.DAYS)
        repository.recordActivity()
        val streak = repository.observeStreak().first()
        assertThat(streak!!.current).isEqualTo(2)
    }

    @Test
    fun `recordActivity resets streak after a missed day`() = runTest {
        repository.recordActivity()
        clock.now = clock.now.plus(2, ChronoUnit.DAYS)
        repository.recordActivity()
        val streak = repository.observeStreak().first()
        assertThat(streak!!.current).isEqualTo(1)
        assertThat(streak.longest).isEqualTo(1)
    }

    @Test
    fun `weak ayahs surfaced from observeWeak`() = runTest {
        repository.markMemorised(1, 1)
        val current = repository.findOne(1, 1)!!
        repository.save(
            current.copy(lastAccuracy = 30, status = MemorisationStatus.REVIEWING),
        )
        val weak = repository.observeWeak(50).first()
        assertThat(weak.map { it.ayahNumber }).containsExactly(1)
    }

    @Test
    fun `save updates progress fields`() = runTest {
        repository.markMemorised(1, 1)
        val current = repository.findOne(1, 1)!!
        val later = Instant.parse("2026-05-01T10:00:00Z")
        repository.save(current.copy(lastAccuracy = 80, lastReviewedAt = later))
        val updated = repository.findOne(1, 1)!!
        assertThat(updated.lastAccuracy).isEqualTo(80)
        assertThat(updated.lastReviewedAt).isEqualTo(later)
    }
}
