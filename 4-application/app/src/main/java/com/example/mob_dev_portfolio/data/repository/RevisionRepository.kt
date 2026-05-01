package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.time.Clock
import com.example.mob_dev_portfolio.data.local.dao.RevisionDao
import com.example.mob_dev_portfolio.domain.model.RevisionTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Read-only view onto the revision schedule. Mutations live on
 * [MemorisationRepository] via the [com.example.mob_dev_portfolio.domain.revision.SpacedRepetitionScheduler].
 */
@Singleton
class RevisionRepository @Inject constructor(
    private val revisionDao: RevisionDao,
    private val clock: Clock,
) {

    fun observeDueNow(): Flow<List<RevisionTask>> {
        val cutoff = clock.now()
        return revisionDao.observeDue(cutoff.toEpochMilli()).map { rows ->
            rows.mapNotNull { row ->
                val due = row.nextReviewAt?.let(Instant::ofEpochMilli) ?: return@mapNotNull null
                RevisionTask(
                    surahNumber = row.surahNumber,
                    ayahNumber = row.ayahNumber,
                    dueAt = due,
                    overdueDays = ChronoUnit.DAYS.between(due, cutoff).toInt(),
                )
            }
        }
    }

    suspend fun countDueNow(): Int = revisionDao.countDue(clock.now().toEpochMilli())
}
