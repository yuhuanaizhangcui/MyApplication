package com.wham.moo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wham.moo.data.entity.MeditationSession
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationSessionDao {
    @Query("SELECT * FROM meditation_sessions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<MeditationSession>>

    @Query("SELECT COUNT(*) FROM meditation_sessions")
    fun getCount(): Flow<Int>

    @Insert
    suspend fun insert(session: MeditationSession)
}