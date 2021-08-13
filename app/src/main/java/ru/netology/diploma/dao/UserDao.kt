package ru.netology.diploma.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.entity.UserEntity
import ru.netology.diploma.enumeration.AttachmentType

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
      fun getAll(): PagingSource<Int, UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List<UserEntity>)

   /* @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
*/
    @Query("DELETE FROM UserEntity")
    suspend fun deleteAll()
}
