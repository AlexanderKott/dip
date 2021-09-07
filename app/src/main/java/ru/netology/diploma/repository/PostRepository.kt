package ru.netology.diploma.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.*


interface AppEntities : UserRepository, PostRepository, EventRepository, JobsRepository,
    AuthMethods, MyPage


interface MyPage {
    suspend fun getMyJobs()
    suspend fun getMyPosts()
}

interface AuthMethods {
    suspend fun authUser(login : String, pass : String, function: (id : Long, token: String) -> Unit)
    suspend fun checkToken() : Boolean
    suspend fun regNewUserWithoutAvatar(
        login: String,
        pass: String,
        name: String,
        success: (id : Long, token: String) -> Unit
    )
}

interface JobsRepository {
    val jdata: Flow<PagingData<Job>>
    suspend fun getJobsById(id : Long)
    suspend fun postNewJob(job :NewJob)
}

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
    suspend fun getWallbyId(id : Long)
    suspend fun getEventbyId(id : Long)
    suspend fun getAllPosts()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post2)
    suspend fun saveWithAttachment(post: Post2, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(post: Post2, upload: MediaUpload?): Long
    suspend fun processWork(id: Long)
}

