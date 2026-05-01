package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.remote.dto.AyahDto
import com.example.mob_dev_portfolio.data.remote.dto.TranslationDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AyahMapperTest {

    @Test
    fun `verse key parses correctly`() {
        val parsed = AyahMapper.parseVerseKey("1:7")
        assertThat(parsed).isEqualTo(1 to 7)
    }

    @Test
    fun `null verse key returns null`() {
        assertThat(AyahMapper.parseVerseKey(null)).isNull()
    }

    @Test
    fun `malformed verse key returns null`() {
        assertThat(AyahMapper.parseVerseKey("hello")).isNull()
        assertThat(AyahMapper.parseVerseKey("1")).isNull()
        assertThat(AyahMapper.parseVerseKey("1:abc")).isNull()
        assertThat(AyahMapper.parseVerseKey("0:1")).isNull()
        assertThat(AyahMapper.parseVerseKey("115:1")).isNull()
    }

    @Test
    fun `dto without verse key is dropped`() {
        val entity = AyahMapper.toEntity(AyahDto(verseKey = null, textUthmani = "x"))
        assertThat(entity).isNull()
    }

    @Test
    fun `first non-blank translation is preserved`() {
        val dto = AyahDto(
            verseKey = "1:1",
            textUthmani = "بسم",
            translations = listOf(TranslationDto(""), TranslationDto("In the name")),
        )
        val entity = AyahMapper.toEntity(dto)!!
        assertThat(entity.textTranslation).isEqualTo("In the name")
    }

    @Test
    fun `null arabic text becomes empty string`() {
        val entity = AyahMapper.toEntity(AyahDto("1:1", null))!!
        assertThat(entity.textArabic).isEmpty()
    }

    @Test
    fun `batch drops invalid rows but keeps the rest`() {
        val list = listOf(
            AyahDto("1:1", "a"),
            AyahDto(null, "b"),
            AyahDto("1:2", "c"),
        )
        val entities = AyahMapper.toEntities(list)
        assertThat(entities).hasSize(2)
    }
}
