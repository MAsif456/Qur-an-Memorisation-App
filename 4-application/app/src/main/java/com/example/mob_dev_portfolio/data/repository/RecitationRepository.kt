package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.data.local.dao.RecitationDao
import com.example.mob_dev_portfolio.data.local.entity.RecitationAttemptEntity
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.newProgress
import com.example.mob_dev_portfolio.domain.model.RecitationResult
import com.example.mob_dev_portfolio.domain.revision.SpacedRepetitionScheduler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists a recitation attempt and advances the SM-2 schedule for the
 * ayah in one call so the attempt log and the schedule cannot drift apart.
 */
@Singleton
class RecitationRepository @Inject constructor(
    private val recitationDao: RecitationDao,
    private val memorisationRepository: MemorisationRepository,
    private val scheduler: SpacedRepetitionScheduler,
    private val clock: Clock,
) {

    suspend fun save(result: RecitationResult): Long {
        val attemptId = recitationDao.insert(
            RecitationAttemptEntity(
                surahNumber = result.surahNumber,
                ayahNumber = result.ayahNumber,
                recordedAt = result.recordedAt.toEpochMilli(),
                accuracy = result.accuracy.value,
                mistakeCount = result.mistakes.size,
                transcript = result.transcript,
                recogniser = result.recogniser,
            ),
        )

        val previous = memorisationRepository.findOne(result.surahNumber, result.ayahNumber)
            ?: newProgress(result.surahNumber, result.ayahNumber)
        val updated = scheduler.next(previous, result.accuracy.value, clock.now())
        // save() also calls recordActivity inside MemorisationRepository, ticking the streak.
        memorisationRepository.save(updated)
        return attemptId
    }
}

/** Test-side helper to read the accuracy column off raw entities. */
internal fun RecitationAttemptEntity.toAccuracy(): Int = accuracy
