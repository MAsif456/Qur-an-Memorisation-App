package com.example.mob_dev_portfolio.data.repository

import com.example.mob_dev_portfolio.core.result.NetworkResult
import com.example.mob_dev_portfolio.data.local.dao.ReciterDao
import com.example.mob_dev_portfolio.data.local.mapper.EntityMapper.toDomain
import com.example.mob_dev_portfolio.data.remote.api.QuranApi
import com.example.mob_dev_portfolio.data.remote.mapper.ReciterMapper
import com.example.mob_dev_portfolio.domain.model.Reciter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Cache-first reciter list, used by Settings. */
@Singleton
class ReciterRepository @Inject constructor(
    private val reciterDao: ReciterDao,
    private val api: QuranApi,
) {

    fun observeAll(): Flow<List<Reciter>> =
        reciterDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    suspend fun refresh(): NetworkResult<Unit> = runCatching {
        val response = api.listReciters()
        val entities = ReciterMapper.toEntities(response.recitations)
        if (entities.isNotEmpty()) {
            reciterDao.upsertAll(entities)
        }
        Unit
    }.fold(
        onSuccess = { NetworkResult.Success(Unit) },
        onFailure = { NetworkResult.Error(it) },
    )
}
