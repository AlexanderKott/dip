package ru.netology.diploma.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.JobEntity

@Dao
interface JobDao {
    @Query("SELECT * FROM JobEntity ORDER BY id DESC")
      fun getAll(): PagingSource<Int,JobEntity>

    @Query("SELECT COUNT(*) == 0 FROM JobEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM JobEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<JobEntity>)

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM JobEntity")
    suspend fun deleteAll()
}

