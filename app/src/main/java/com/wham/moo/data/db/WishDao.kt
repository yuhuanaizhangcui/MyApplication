package com.wham.moo.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wham.moo.data.entity.Wish
import kotlinx.coroutines.flow.Flow

@Dao
interface WishDao {
    @Query("SELECT * FROM wishes ORDER BY id DESC")
    fun getAll(): Flow<List<Wish>>

    @Query("SELECT COUNT(*) FROM wishes WHERE status = 'done'")
    fun getCompletedCount(): Flow<Int>

    @Insert
    suspend fun insert(wish: Wish)

    @Update
    suspend fun update(wish: Wish)

    @Delete
    suspend fun delete(wish: Wish)

    @Query("DELETE FROM wishes WHERE id = :id")
    suspend fun deleteById(id: Long)
}