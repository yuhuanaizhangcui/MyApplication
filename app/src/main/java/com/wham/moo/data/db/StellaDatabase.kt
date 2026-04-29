package com.wham.moo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.data.entity.MeditationSession
import com.wham.moo.data.entity.Wish

@Database(
    entities = [DiaryEntry::class, Wish::class, MeditationSession::class],
    version = 1,
    exportSchema = false
)
abstract class StellaDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun wishDao(): WishDao
    abstract fun meditationSessionDao(): MeditationSessionDao

    companion object {
        @Volatile
        private var INSTANCE: StellaDatabase? = null

        fun getDatabase(context: Context): StellaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    StellaDatabase::class.java,
                    "stella_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}