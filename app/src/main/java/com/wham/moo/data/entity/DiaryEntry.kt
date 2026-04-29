package com.wham.moo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val mood: String,
    val content: String,
    val time: String,
    val createdAt: Long = System.currentTimeMillis()
)