package com.example.mob_dev_portfolio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.data.local.entity.ChapterAudioEntity

/** DAO for cached per-chapter audio URLs. The cache may be sparse. */
@Dao
interface ChapterAudioDao {

    @Query(
        "SELECT * FROM chapter_audio WHERE reciterId = :reciterId " +
            "AND surahNumber = :surahNumber LIMIT 1",
    )
    suspend fun find(reciterId: Int, surahNumber: Int): ChapterAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(audio: ChapterAudioEntity)

    @Query("DELETE FROM chapter_audio WHERE reciterId = :reciterId")
    suspend fun deleteForReciter(reciterId: Int)
}
