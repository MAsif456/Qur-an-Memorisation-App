package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.ReciterEntity
import kotlinx.coroutines.flow.Flow

/** DAO for cached reciter metadata. */
@Dao
interface ReciterDao {

    @Query("SELECT * FROM reciters ORDER BY name ASC")
    fun observeAll(): Flow<List<ReciterEntity>>

    @Query("SELECT * FROM reciters WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): ReciterEntity?

    @Query("SELECT COUNT(*) FROM reciters")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reciters: List<ReciterEntity>)

    @Query("DELETE FROM reciters")
    suspend fun deleteAll()
}
