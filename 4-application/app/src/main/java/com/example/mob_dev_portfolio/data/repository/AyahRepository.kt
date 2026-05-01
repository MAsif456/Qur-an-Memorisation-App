package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.result.NetworkResult
import com.example.mob_dev_portfolio.data.local.dao.AyahDao
import com.example.mob_dev_portfolio.data.local.dao.ChapterAudioDao
import com.example.mob_dev_portfolio.data.local.entity.ChapterAudioEntity
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.toDomain
import com.example.mob_dev_portfolio.data.remote.api.QuranApi
import com.example.mob_dev_portfolio.data.remote.mapper.AyahMapper
import com.example.mob_dev_portfolio.domain.model.Ayah
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Cache-first ayah text and audio URLs. */
@Singleton
class AyahRepository @Inject constructor(
    private val ayahDao: AyahDao,
    private val chapterAudioDao: ChapterAudioDao,
    private val api: QuranApi,
) {

    fun observeBySurah(surahNumber: Int): Flow<List<Ayah>> =
        ayahDao.observeBySurah(surahNumber).map { rows -> rows.map { it.toDomain() } }

    suspend fun findOne(surahNumber: Int, ayahNumber: Int): Ayah? =
        ayahDao.findOne(surahNumber, ayahNumber)?.toDomain()

    suspend fun refreshSurah(surahNumber: Int): NetworkResult<Unit> = runCatching {
        val response = api.listAyahs(surahNumber)
        val entities = AyahMapper.toEntities(response.verses)
        if (entities.isNotEmpty()) {
            ayahDao.upsertAll(entities)
        }
        Unit
    }.fold(
        onSuccess = { NetworkResult.Success(Unit) },
        onFailure = { NetworkResult.Error(it) },
    )

    /** Cache-first; falls back to a network fetch and caches the result on success. */
    suspend fun audioUrl(reciterId: Int, surahNumber: Int): String? {
        val cached = chapterAudioDao.find(reciterId, surahNumber)
        if (cached != null) return cached.audioUrl
        return runCatching {
            val url = api.getChapterAudio(reciterId, surahNumber).audioFile?.audioUrl
            if (!url.isNullOrBlank()) {
                chapterAudioDao.upsert(ChapterAudioEntity(reciterId, surahNumber, url))
            }
            url
        }.getOrNull()
    }
}
