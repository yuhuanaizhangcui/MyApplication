package com.wham.moo.data.repository

import com.wham.moo.data.db.DiaryEntryDao
import com.wham.moo.data.db.MeditationSessionDao
import com.wham.moo.data.db.WishDao
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.data.entity.MeditationSession
import com.wham.moo.data.entity.Wish
import kotlinx.coroutines.flow.Flow

class StellaRepository(
    private val diaryDao: DiaryEntryDao,
    private val wishDao: WishDao,
    private val meditationDao: MeditationSessionDao
) {
    val allDiaries: Flow<List<DiaryEntry>> = diaryDao.getAll()
    val allWishes: Flow<List<Wish>> = wishDao.getAll()
    val diaryCount: Flow<Int> = diaryDao.getCount()
    val meditationCount: Flow<Int> = meditationDao.getCount()
    val completedWishCount: Flow<Int> = wishDao.getCompletedCount()
    val allDiaryDates: Flow<List<String>> = diaryDao.getAllDates()

    fun getDiariesByDate(date: String): Flow<List<DiaryEntry>> = diaryDao.getByDate(date)

    suspend fun addDiary(entry: DiaryEntry) = diaryDao.insert(entry)
    suspend fun deleteDiary(id: Long) = diaryDao.deleteById(id)
    suspend fun getTodayMood(date: String) = diaryDao.getTodayMood(date)
    suspend fun deleteTodayMood(date: String) = diaryDao.deleteTodayMood(date)

    suspend fun addWish(wish: Wish) = wishDao.insert(wish)
    suspend fun updateWish(wish: Wish) = wishDao.update(wish)
    suspend fun deleteWish(id: Long) = wishDao.deleteById(id)

    suspend fun addMeditation(session: MeditationSession) = meditationDao.insert(session)
}