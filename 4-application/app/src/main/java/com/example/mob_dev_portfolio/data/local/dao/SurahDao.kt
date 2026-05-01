package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the cached Surah catalogue.
 *
 * Reads return [Flow] so the UI re-renders when the seeder or the network
 * refresh inserts updated rows. Writes are upserts so re-running the seeder
 * is safe.
 */
@Dao
interface SurahDao {

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun observeAll(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    fun observeByNumber(number: Int): Flow<SurahEntity?>

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(surahs: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(surah: SurahEntity)

    @Query("DELETE FROM surahs")
    suspend fun deleteAll()
}
