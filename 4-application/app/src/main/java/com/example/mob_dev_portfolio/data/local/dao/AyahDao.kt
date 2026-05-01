package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for cached ayah text. The cache may be partially populated: only Surahs
 * the user has opened are guaranteed to have full ayah rows present.
 */
@Dao
interface AyahDao {

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun observeBySurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query(
        "SELECT * FROM ayahs WHERE surahNumber = :surahNumber " +
            "AND ayahNumber = :ayahNumber LIMIT 1",
    )
    suspend fun findOne(surahNumber: Int, ayahNumber: Int): AyahEntity?

    @Query("SELECT COUNT(*) FROM ayahs WHERE surahNumber = :surahNumber")
    suspend fun countInSurah(surahNumber: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(ayahs: List<AyahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ayah: AyahEntity)

    @Query("DELETE FROM ayahs WHERE surahNumber = :surahNumber")
    suspend fun deleteBySurah(surahNumber: Int)
}
