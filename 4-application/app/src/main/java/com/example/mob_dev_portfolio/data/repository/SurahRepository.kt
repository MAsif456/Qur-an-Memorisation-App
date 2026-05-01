package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.result.NetworkResult
import com.example.mob_dev_portfolio.data.local.dao.SurahDao
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.toDomain
import com.example.mob_dev_portfolio.data.remote.api.QuranApi
import com.example.mob_dev_portfolio.data.remote.mapper.SurahMapper
import com.example.mob_dev_portfolio.domain.model.Surah
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache-first Surah catalogue.
 *
 * Reads come from Room (pre-seeded on first launch). [refresh] updates the
 * cache from the API; the UI re-renders automatically through the Flow.
 */
@Singleton
class SurahRepository @Inject constructor(
    private val surahDao: SurahDao,
    private val api: QuranApi,
) {

    fun observeAll(): Flow<List<Surah>> =
        surahDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    suspend fun refresh(): NetworkResult<Unit> = runCatching {
        val response = api.listSurahs()
        val entities = SurahMapper.toEntities(response.chapters)
        if (entities.isNotEmpty()) {
            surahDao.upsertAll(entities)
        }
        Unit
    }.fold(
        onSuccess = { NetworkResult.Success(Unit) },
        onFailure = { NetworkResult.Error(it) },
    )
}
