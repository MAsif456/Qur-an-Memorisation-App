package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.remote.dto.SurahDto
import com.example.mob_dev_portfolio.data.remote.dto.TranslatedNameDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SurahMapperTest {

    @Test
    fun `complete dto maps to all entity fields`() {
        val dto = SurahDto(
            id = 1,
            nameArabic = "الفاتحة",
            nameSimple = "Al-Fatihah",
            translatedName = TranslatedNameDto("The Opening"),
            versesCount = 7,
            revelationPlace = "makkah",
        )
        val entity = SurahMapper.toEntity(dto)
        assertThat(entity.number).isEqualTo(1)
        assertThat(entity.nameArabic).isEqualTo("الفاتحة")
        assertThat(entity.nameTransliterated).isEqualTo("Al-Fatihah")
        assertThat(entity.nameTranslation).isEqualTo("The Opening")
        assertThat(entity.ayahCount).isEqualTo(7)
        assertThat(entity.revelationPlace).isEqualTo("makkah")
    }

    @Test
    fun `null fields fall back to safe defaults`() {
        val dto = SurahDto(2, null, null, null, null, null)
        val entity = SurahMapper.toEntity(dto)
        assertThat(entity.nameArabic).isEmpty()
        assertThat(entity.nameTransliterated).isEmpty()
        assertThat(entity.nameTranslation).isEmpty()
        assertThat(entity.ayahCount).isEqualTo(0)
        assertThat(entity.revelationPlace).isEqualTo("unknown")
    }

    @Test
    fun `out of range ids are dropped from the batch`() {
        val good = SurahDto(1, "x", "x", null, 1, "makkah")
        val zero = SurahDto(0, "y", "y", null, 1, "makkah")
        val tooBig = SurahDto(115, "z", "z", null, 1, "makkah")
        val entities = SurahMapper.toEntities(listOf(good, zero, tooBig))
        assertThat(entities).hasSize(1)
        assertThat(entities.single().number).isEqualTo(1)
    }

    @Test
    fun `negative verse count clamped to zero`() {
        val dto = SurahDto(3, "x", "x", null, -5, "madinah")
        val entity = SurahMapper.toEntity(dto)
        assertThat(entity.ayahCount).isEqualTo(0)
    }
}
