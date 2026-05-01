package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.data.local.dao.MemorisationDao
import com.example.mob_dev_portfolio.data.local.dao.StreakDao
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.newProgress
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.toDomain
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.toEntity
import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.domain.model.Streak
import com.example.mob_dev_portfolio.domain.streaks.StreakCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single mutator for ayah progress and the streak. Callers cannot bypass
 * status promotion or streak updates by going through the DAO directly.
 */
@Singleton
class MemorisationRepository @Inject constructor(
    private val memorisationDao: MemorisationDao,
    private val streakDao: StreakDao,
    private val clock: Clock,
) {

    fun observeAll(): Flow<List<AyahProgress>> =
        memorisationDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeByStatus(status: MemorisationStatus): Flow<List<AyahProgress>> =
        memorisationDao.observeByStatus(status).map { rows -> rows.map { it.toDomain() } }

    fun observeWeak(threshold: Int): Flow<List<AyahProgress>> =
        memorisationDao.observeWeakAyahs(threshold).map { rows -> rows.map { it.toDomain() } }

    fun observeTouchedCount(): Flow<Int> = memorisationDao.observeTouchedCount()

    fun observeOne(surah: Int, ayah: Int): Flow<AyahProgress?> =
        memorisationDao.observeOne(surah, ayah).map { it?.toDomain() }

    fun observeStreak(): Flow<Streak?> = streakDao.observe().map { it?.toDomain() }

    suspend fun findOne(surah: Int, ayah: Int): AyahProgress? =
        memorisationDao.findOne(surah, ayah)?.toDomain()

    /** No-op if the ayah is already past NEW. */
    suspend fun markMemorised(surah: Int, ayah: Int) {
        val current = memorisationDao.findOne(surah, ayah)?.toDomain() ?: newProgress(surah, ayah)
        if (current.status != MemorisationStatus.NEW) return
        val updated = current.copy(
            status = MemorisationStatus.LEARNING,
            lastReviewedAt = clock.now(),
        )
        memorisationDao.upsert(updated.toEntity())
        recordActivity()
    }

    suspend fun unmark(surah: Int, ayah: Int) {
        memorisationDao.delete(surah, ayah)
    }

    suspend fun save(progress: AyahProgress) {
        memorisationDao.upsert(progress.toEntity())
        recordActivity()
    }

    suspend fun recordActivity() {
        val today = clock.now().atZone(ZoneId.systemDefault()).toLocalDate()
        val previous = streakDao.get()?.toDomain() ?: Streak(0, 0, null)
        val next = StreakCalculator.update(previous, today)
        streakDao.upsert(next.toEntity())
    }
}
