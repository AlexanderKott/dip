package ru.netology.diploma.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.*


interface AppEntities : UserRepository, PostRepository, EventRepository


interface EventRepository {
    val edata: Flow<PagingData<Event>>
    suspend fun getAllEvents()
}

interface UserRepository {
    val udata: Flow<PagingData<User>>
    suspend fun getAllUsers()
}


interface PostRepository   {
    val pdata: Flow<PagingData<Post2>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post2)
    suspend fun saveWithAttachment(post: Post2, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(post: Post2, upload: MediaUpload?): Long
    suspend fun processWork(id: Long)
}

