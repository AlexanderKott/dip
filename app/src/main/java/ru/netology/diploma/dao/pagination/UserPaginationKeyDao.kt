package ru.netology.diploma.dao.pagination

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diploma.entity.PostKeyEntry
import ru.netology.diploma.entity.UserKeyEntry

@Dao
interface UserPaginationKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listOf: List<UserKeyEntry>)

    @Query ("SELECT MIN(id) FROM UserKeyEntry")
    suspend fun min(): Long?

    @Query ("SELECT MAX(id) FROM UserKeyEntry")
    suspend fun max(): Long?
}