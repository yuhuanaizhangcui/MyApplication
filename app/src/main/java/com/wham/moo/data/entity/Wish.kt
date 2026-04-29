package com.wham.moo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishes")
data class Wish(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val status: String,
    val progress: Int
)