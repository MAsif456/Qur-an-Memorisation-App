package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.remote.dto.ReciterDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReciterMapperTest {

    @Test
    fun `reciter without name dropped`() {
        assertThat(ReciterMapper.toEntity(ReciterDto(1, null, null))).isNull()
    }

    @Test
    fun `blank name treated as missing`() {
        assertThat(ReciterMapper.toEntity(ReciterDto(1, "  ", null))).isNull()
    }

    @Test
    fun `complete dto maps all fields`() {
        val entity = ReciterMapper.toEntity(ReciterDto(7, "Mishari", "Murattal"))!!
        assertThat(entity.id).isEqualTo(7)
        assertThat(entity.name).isEqualTo("Mishari")
        assertThat(entity.style).isEqualTo("Murattal")
    }

    @Test
    fun `batch filters invalid entries`() {
        val list = listOf(
            ReciterDto(1, "A", null),
            ReciterDto(2, null, null),
            ReciterDto(3, "C", "Style"),
        )
        val entities = ReciterMapper.toEntities(list)
        assertThat(entities.map { it.id }).containsExactly(1, 3)
    }
}
