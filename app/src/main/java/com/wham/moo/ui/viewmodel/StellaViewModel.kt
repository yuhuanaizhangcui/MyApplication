package com.wham.moo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wham.moo.StellaApplication
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.data.entity.MeditationSession
import com.wham.moo.data.entity.Wish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StellaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as StellaApplication).repository

    val allDiaries = repository.allDiaries.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allWishes = repository.allWishes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val diaryCount = repository.diaryCount.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val meditationCount = repository.meditationCount.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val completedWishCount = repository.completedWishCount.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _selectedDate = MutableStateFlow(formatDate(Calendar.getInstance()))
    val selectedDate: StateFlow<String> = _selectedDate

    val diariesForSelectedDate = combine(
        allDiaries, _selectedDate
    ) { diaries, date ->
        diaries.filter { it.date == date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weekChartData = allDiaries.map { diaries ->
        val weekData = IntArray(7) { 0 }
        diaries.forEach { d ->
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(d.date) ?: return@forEach
                val cal = Calendar.getInstance().apply { time = date }
                val day = cal.get(Calendar.DAY_OF_WEEK)
                val idx = if (day == Calendar.SUNDAY) 6 else day - 2
                if (idx in 0..6) weekData[idx]++
            } catch (_: Exception) { }
        }
        weekData.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), List(7) { 0 })

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun addDiary(content: String, mood: String, date: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        viewModelScope.launch {
            repository.addDiary(DiaryEntry(date = date, mood = mood, content = content, time = time))
        }
    }

    fun addOrUpdateMood(mood: String, date: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        val label = moodToLabel(mood)
        viewModelScope.launch {
            repository.deleteTodayMood(date)
            repository.addDiary(DiaryEntry(date = date, mood = mood, content = "今日心情：$label", time = time))
        }
    }

    fun deleteDiary(id: Long) {
        viewModelScope.launch { repository.deleteDiary(id) }
    }

    fun addWish(title: String, progress: Int) {
        val status = if (progress >= 100) "done" else if (progress > 0) "active" else "pending"
        viewModelScope.launch {
            repository.addWish(Wish(title = title, status = status, progress = progress))
        }
    }

    fun toggleWishStatus(wish: Wish) {
        val updated = if (wish.status == "done") {
            wish.copy(status = "active", progress = wish.progress.coerceAtMost(99))
        } else {
            wish.copy(status = "done", progress = 100)
        }
        viewModelScope.launch { repository.updateWish(updated) }
    }

    fun deleteWish(id: Long) {
        viewModelScope.launch { repository.deleteWish(id) }
    }

    fun addMeditationSession(durationMinutes: Int) {
        val date = formatDate(Calendar.getInstance())
        viewModelScope.launch {
            repository.addMeditation(MeditationSession(date = date, durationMinutes = durationMinutes))
        }
    }
}

fun formatDate(cal: Calendar): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(cal.time)
}

fun moodToLabel(mood: String): String = when (mood) {
    "calm" -> "平静"
    "happy" -> "开心"
    "sad" -> "低落"
    "anxious" -> "焦虑"
    else -> "平静"
}