package com.wham.moo

import android.app.Application
import com.wham.moo.data.db.StellaDatabase
import com.wham.moo.data.repository.StellaRepository

class StellaApplication : Application() {
    val database by lazy { StellaDatabase.getDatabase(this) }
    val repository by lazy {
        StellaRepository(
            database.diaryEntryDao(),
            database.wishDao(),
            database.meditationSessionDao()
        )
    }
}