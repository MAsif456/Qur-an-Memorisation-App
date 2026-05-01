package com.example.mob_dev_portfolio.data.remote.mapper

import com.example.mob_dev_portfolio.data.local.entity.ReciterEntity
import com.example.mob_dev_portfolio.data.remote.dto.ReciterDto

/** Drops reciters with no name (we can't display them). */
object ReciterMapper {

    fun toEntity(dto: ReciterDto): ReciterEntity? {
        val name = dto.reciterName?.takeIf { it.isNotBlank() } ?: return null
        return ReciterEntity(id = dto.id, name = name, style = dto.style)
    }

    fun toEntities(dtos: List<ReciterDto>): List<ReciterEntity> =
        dtos.mapNotNull(::toEntity)
}
