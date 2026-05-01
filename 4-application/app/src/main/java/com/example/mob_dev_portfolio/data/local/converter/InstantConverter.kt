package com.example.mob_dev_portfolio.data.local.converter

import androidx.room.TypeConverter
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import java.time.Instant

/**
 * Room converters. `Instant` is stored as epoch millis;
 * [MemorisationStatus] is stored as the enum constant name.
 */
class InstantConverter {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromStatus(value: MemorisationStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): MemorisationStatus = MemorisationStatus.valueOf(value)
}
