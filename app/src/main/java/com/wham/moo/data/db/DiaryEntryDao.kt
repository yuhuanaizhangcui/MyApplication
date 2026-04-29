package com.wham.moo.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wham.moo.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY createdAt DESC")
    fun getByDate(date: String): Flow<List<DiaryEntry>>

    @Query("SELECT COUNT(*) FROM diary_entries")
    fun getCount(): Flow<Int>

    @Query("SELECT date FROM diary_entries")
    fun getAllDates(): Flow<List<String>>

    @Insert
    suspend fun insert(entry: DiaryEntry)

    @Delete
    suspend fun delete(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM diary_entries WHERE date = :date AND content LIKE '今日心情：%' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getTodayMood(date: String): DiaryEntry?

    @Query("DELETE FROM diary_entries WHERE date = :date AND content LIKE '今日心情：%'")
    suspend fun deleteTodayMood(date: String)
}