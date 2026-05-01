package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.RecitationAttemptEntity
import kotlinx.coroutines.flow.Flow

/** DAO for the append-only recitation attempts log. */
@Dao
interface RecitationDao {

    @Query("SELECT * FROM recitation_attempts ORDER BY recordedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RecitationAttemptEntity>>

    @Query(
        "SELECT * FROM recitation_attempts WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber ORDER BY recordedAt DESC",
    )
    fun observeForAyah(surahNumber: Int, ayahNumber: Int): Flow<List<RecitationAttemptEntity>>

    @Query(
        "SELECT * FROM recitation_attempts WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber ORDER BY recordedAt DESC LIMIT 1",
    )
    suspend fun mostRecentForAyah(surahNumber: Int, ayahNumber: Int): RecitationAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: RecitationAttemptEntity): Long

    @Query("DELETE FROM recitation_attempts")
    suspend fun deleteAll()
}
