package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton streak record. The table is constrained to a single row by hard
 * coding [SINGLETON_ID] as the primary key.
 */
@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val current: Int,
    val longest: Int,
    val lastActiveDateEpochDay: Long?,
) {
    companion object {
        const val SINGLETON_ID: Int = 0
    }
}
