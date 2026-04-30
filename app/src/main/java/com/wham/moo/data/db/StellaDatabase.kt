package com.wham.moo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.data.entity.MeditationSession
import com.wham.moo.data.entity.Wish

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE diary_entries ADD COLUMN imageUris TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [DiaryEntry::class, Wish::class, MeditationSession::class],
    version = 2,
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}