package com.example.mob_dev_portfolio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached reciter from the Quran.com `recitations` endpoint. */
@Entity(tableName = "reciters")
data class ReciterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val style: String?,
)
