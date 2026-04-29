package com.wham.moo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditation_sessions")
data class MeditationSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val durationMinutes: Int,
    val createdAt: Long = System.currentTimeMillis()
)