package com.example.mob_dev_portfolio.data.remote.api

import com.example.mob_dev_portfolio.data.remote.dto.AyahResponseDto
import com.example.mob_dev_portfolio.data.remote.dto.ChapterAudioResponseDto
import com.example.mob_dev_portfolio.data.remote.dto.ReciterListResponseDto
import com.example.mob_dev_portfolio.data.remote.dto.SurahListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Quran.com API.
 *
 * Repositories convert the suspend exceptions raised here into
 * [com.example.mob_dev_portfolio.core.result.NetworkResult].
 */
interface QuranApi {

    @GET("api/v4/chapters")
    suspend fun listSurahs(@Query("language") language: String = "en"): SurahListResponseDto

    @GET("api/v4/verses/by_chapter/{chapter}")
    suspend fun listAyahs(
        @Path("chapter") chapter: Int,
        @Query("language") language: String = "en",
        @Query("words") words: Boolean = false,
        @Query("translations") translationIds: String = DEFAULT_TRANSLATION_ID,
        @Query("fields") fields: String = "text_uthmani",
        @Query("per_page") perPage: Int = MAX_AYAHS_PER_PAGE,
    ): AyahResponseDto

    @GET("api/v4/resources/recitations")
    suspend fun listReciters(@Query("language") language: String = "en"): ReciterListResponseDto

    @GET("api/v4/chapter_recitations/{reciterId}/{chapter}")
    suspend fun getChapterAudio(
        @Path("reciterId") reciterId: Int,
        @Path("chapter") chapter: Int,
    ): ChapterAudioResponseDto

    companion object {
        // Saheeh International English translation.
        const val DEFAULT_TRANSLATION_ID: String = "131"
        const val MAX_AYAHS_PER_PAGE: Int = 286
        const val BASE_URL: String = "https://api.quran.com/"
    }
}
