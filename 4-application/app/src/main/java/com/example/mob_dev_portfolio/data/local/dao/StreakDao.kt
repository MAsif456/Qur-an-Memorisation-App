package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

/** DAO for the singleton streak record (always operates on row id 0). */
@Dao
interface StreakDao {

    /** Emits null until the user has logged any review. */
    @Query("SELECT * FROM streak WHERE id = 0 LIMIT 1")
    fun observe(): Flow<StreakEntity?>

    @Query("SELECT * FROM streak WHERE id = 0 LIMIT 1")
    suspend fun get(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streak: StreakEntity)
}
