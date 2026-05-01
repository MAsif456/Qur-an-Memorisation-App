package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.AyahProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for revision queries. Shares the `ayah_progress` table with
 * [MemorisationDao] but only exposes the "what's due" reads.
 */
@Dao
interface RevisionDao {

    /** Ayahs whose nextReviewAt is at or before the cutoff, most overdue first. */
    @Query(
        "SELECT * FROM ayah_progress WHERE status != 'NEW' " +
            "AND nextReviewAt IS NOT NULL AND nextReviewAt <= :cutoffEpochMillis " +
            "ORDER BY nextReviewAt ASC",
    )
    fun observeDue(cutoffEpochMillis: Long): Flow<List<AyahProgressEntity>>

    @Query(
        "SELECT COUNT(*) FROM ayah_progress WHERE status != 'NEW' " +
            "AND nextReviewAt IS NOT NULL AND nextReviewAt <= :cutoffEpochMillis",
    )
    suspend fun countDue(cutoffEpochMillis: Long): Int
}
