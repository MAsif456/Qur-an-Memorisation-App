package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.AyahProgressEntity
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for per-ayah progress and the SM-2 schedule. Mutations go through the
 * Memorisation, Revision, and Recitation repositories; the UI never writes
 * here directly.
 */
@Dao
interface MemorisationDao {

    @Query("SELECT * FROM ayah_progress ORDER BY lastReviewedAt DESC")
    fun observeAll(): Flow<List<AyahProgressEntity>>

    @Query(
        "SELECT * FROM ayah_progress WHERE status = :status " +
            "ORDER BY lastReviewedAt DESC",
    )
    fun observeByStatus(status: MemorisationStatus): Flow<List<AyahProgressEntity>>

    @Query(
        "SELECT * FROM ayah_progress WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber LIMIT 1",
    )
    suspend fun findOne(surahNumber: Int, ayahNumber: Int): AyahProgressEntity?

    @Query(
        "SELECT * FROM ayah_progress WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber LIMIT 1",
    )
    fun observeOne(surahNumber: Int, ayahNumber: Int): Flow<AyahProgressEntity?>

    @Query("SELECT COUNT(*) FROM ayah_progress WHERE status != 'NEW'")
    fun observeTouchedCount(): Flow<Int>

    @Query(
        "SELECT * FROM ayah_progress WHERE lastAccuracy IS NOT NULL " +
            "AND lastAccuracy < :threshold ORDER BY lastAccuracy ASC",
    )
    fun observeWeakAyahs(threshold: Int): Flow<List<AyahProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: AyahProgressEntity)

    @Query(
        "DELETE FROM ayah_progress WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber",
    )
    suspend fun delete(surahNumber: Int, ayahNumber: Int)
}
