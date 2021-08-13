package ru.netology.diploma.dao.old

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diploma.entity.PostWorkEntity

@Dao
interface PostWorkDao {
    @Query("SELECT * FROM PostWorkEntity WHERE id = :id")
    suspend fun getById(id: Long): PostWorkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(work: PostWorkEntity): Long

    @Query("DELETE FROM PostWorkEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}