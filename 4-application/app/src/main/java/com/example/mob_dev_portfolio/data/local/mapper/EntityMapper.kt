package com.example.mob_dev_portfolio.data.local.mapper

import com.example.mob_dev_portfolio.data.local.entity.AyahEntity
import com.example.mob_dev_portfolio.data.local.entity.AyahProgressEntity
import com.example.mob_dev_portfolio.data.local.entity.RecitationAttemptEntity
import com.example.mob_dev_portfolio.data.local.entity.ReciterEntity
import com.example.mob_dev_portfolio.data.local.entity.StreakEntity
import com.example.mob_dev_portfolio.data.local.entity.SurahEntity
import com.example.mob_dev_portfolio.domain.model.Ayah
import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.domain.model.Reciter
import com.example.mob_dev_portfolio.domain.model.Streak
import com.example.mob_dev_portfolio.domain.model.Surah
import java.time.Instant
import java.time.LocalDate

/** Pure mappers between Room entities and domain types. */
object EntityMapper {

    private const val DEFAULT_EF: Double = 2.5

    fun SurahEntity.toDomain(): Surah = Surah(
        number = number,
        nameArabic = nameArabic,
        nameTransliterated = nameTransliterated,
        nameTranslation = nameTranslation,
        ayahCount = ayahCount,
        revelationPlace = revelationPlace,
    )

    fun AyahEntity.toDomain(): Ayah = Ayah(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        textArabic = textArabic,
        textTranslation = textTranslation,
    )

    fun ReciterEntity.toDomain(): Reciter = Reciter(id = id, name = name, style = style)

    fun StreakEntity.toDomain(): Streak = Streak(
        current = current,
        longest = longest,
        lastActiveDate = lastActiveDateEpochDay?.let(LocalDate::ofEpochDay),
    )

    fun Streak.toEntity(): StreakEntity = StreakEntity(
        current = current,
        longest = longest,
        lastActiveDateEpochDay = lastActiveDate?.toEpochDay(),
    )

    fun AyahProgressEntity.toDomain(): AyahProgress = AyahProgress(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        status = status,
        repetitions = repetitions,
        easinessFactor = easinessFactor,
        intervalDays = intervalDays,
        lastReviewedAt = lastReviewedAt?.let(Instant::ofEpochMilli),
        nextReviewAt = nextReviewAt?.let(Instant::ofEpochMilli),
        lastAccuracy = lastAccuracy,
    )

    fun AyahProgress.toEntity(): AyahProgressEntity = AyahProgressEntity(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        status = status,
        repetitions = repetitions,
        easinessFactor = easinessFactor,
        intervalDays = intervalDays,
        lastReviewedAt = lastReviewedAt?.toEpochMilli(),
        nextReviewAt = nextReviewAt?.toEpochMilli(),
        lastAccuracy = lastAccuracy,
    )

    fun newProgress(surah: Int, ayah: Int): AyahProgress = AyahProgress(
        surahNumber = surah,
        ayahNumber = ayah,
        status = MemorisationStatus.NEW,
        repetitions = 0,
        easinessFactor = DEFAULT_EF,
        intervalDays = 0,
        lastReviewedAt = null,
        nextReviewAt = null,
        lastAccuracy = null,
    )

    fun RecitationAttemptEntity.toDomainAccuracy(): Int = accuracy
}
