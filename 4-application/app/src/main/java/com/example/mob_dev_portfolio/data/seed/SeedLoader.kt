package com.example.mob_dev_portfolio.data.seed

import android.content.Context
import com.example.mob_dev_portfolio.data.local.dao.AyahDao
import com.example.mob_dev_portfolio.data.local.dao.ChapterAudioDao
import com.example.mob_dev_portfolio.data.local.dao.ReciterDao
import com.example.mob_dev_portfolio.data.local.dao.SurahDao
import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import com.example.mob_dev_portfolio.data.local.entity.ChapterAudioEntity
import com.example.mob_dev_portfolio.data.local.entity.ReciterEntity
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads bundled Quran data into Room on first launch.
 *
 * Idempotent: [seedIfRequired] returns immediately if the Surah catalogue is
 * already populated. The caller catches and logs any parse error so a bad
 * asset can't crash start-up.
 */
@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val reciterDao: ReciterDao,
    private val chapterAudioDao: ChapterAudioDao,
    private val moshi: Moshi,
) {

    suspend fun seedIfRequired() = withContext(Dispatchers.IO) {
        if (surahDao.count() > 0) return@withContext
        val seed = readSeed() ?: return@withContext
        applySeed(seed)
    }

    internal fun readSeed(): SeedDto? {
        val adapter = moshi.adapter(SeedDto::class.java)
        return context.assets.open(SEED_ASSET_PATH).bufferedReader().use { reader ->
            adapter.fromJson(reader.readText())
        }
    }

    private suspend fun applySeed(seed: SeedDto) {
        surahDao.upsertAll(seed.surahs.map { it.toEntity() })
        if (seed.reciters.isNotEmpty()) {
            reciterDao.upsertAll(seed.reciters.map { it.toEntity() })
        }
        if (seed.ayahs.isNotEmpty()) {
            ayahDao.upsertAll(seed.ayahs.map { it.toEntity() })
        }
        seed.chapterAudios.forEach { chapterAudioDao.upsert(it.toEntity()) }
    }

    private fun SeedSurahDto.toEntity(): SurahEntity = SurahEntity(
        number = number,
        nameArabic = nameArabic,
        nameTransliterated = nameTransliterated,
        nameTranslation = nameTranslation,
        ayahCount = ayahCount,
        revelationPlace = revelationPlace,
    )

    private fun SeedReciterDto.toEntity(): ReciterEntity =
        ReciterEntity(id = id, name = name, style = style)

    private fun SeedAyahDto.toEntity(): AyahEntity = AyahEntity(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        textArabic = textArabic,
        textTranslation = textTranslation,
    )

    private fun SeedChapterAudioDto.toEntity(): ChapterAudioEntity =
        ChapterAudioEntity(reciterId = reciterId, surahNumber = surahNumber, audioUrl = audioUrl)

    internal companion object {
        const val SEED_ASSET_PATH: String = "surahs_seed.json"
    }
}
