package ru.netology.diploma.repository

import android.content.Context
import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.diploma.adapter.events.EventsRemoteMediator
import ru.netology.diploma.adapter.posts.PostRemoteMediator
import ru.netology.diploma.adapter.users.UserRemoteMediator
import ru.netology.diploma.api.*
import ru.netology.diploma.db.AppDb
import ru.netology.diploma.dto.*
import ru.netology.diploma.enumeration.AttachmentType
import java.io.IOException
import ru.netology.diploma.adapter.jobs.JobsRemoteMediator
import ru.netology.diploma.entity.*
import ru.netology.diploma.error.*
import java.net.ConnectException
import java.net.SocketTimeoutException


class PostRepositoryImpl(
    private val base: AppDb,
    private val api: ApiService,
    private val context: Context
) : AppEntities {


    @ExperimentalPagingApi
    override val pdata: Flow<PagingData<Post2>> = Pager(
        remoteMediator = PostRemoteMediator(api, base),
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = base.postDao()::getAll
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    @ExperimentalPagingApi
    override val udata: Flow<PagingData<User>> = Pager(
        remoteMediator = UserRemoteMediator(api, base),
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = base.userDao()::getAll
    ).flow.map {
        it.map(UserEntity::toDto)
    }

    @ExperimentalPagingApi
    override val edata: Flow<PagingData<Event>> = Pager(
        remoteMediator = EventsRemoteMediator(api, base),
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = base.eventDao()::getAll
    ).flow.map {
        it.map(EventEntity::toDto)
    }


    @ExperimentalPagingApi
    override val jdata: Flow<PagingData<Job>> = Pager(
        remoteMediator = JobsRemoteMediator(api, context, base),
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = base.jobDao()::getAll
    ).flow.map {
        it.map(JobEntity::toDto)
    }



    override suspend fun authUser(login: String, pass: String, success: (id : Long, token: String) -> Unit) {
        netRequestWrapper(object {}.javaClass.enclosingMethod.name) {
            val response = api.authMe(login, pass)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()
            if (body?.token != null) {
                success(body.id, body.token)
            }
        }
    }

    override suspend fun checkToken() : Boolean{
        return try {
            val response = api.checkToken()
            response.code() != 405 && response.code() < 500
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun regNewUserWithoutAvatar(
        login: String,
        pass: String,
        name: String,
        successCase: (id : Long, token: String) -> Unit
    ) {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.regMeWithoutAvatar(login, pass, name)
            val body = response.body()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (body?.token != null) {
                successCase(body.id, body.token)
            }
        }
    }

    override suspend fun getMyJobs() {
        TODO("Not yet implemented")
    }

    override suspend fun getMyPosts() {
        TODO("Not yet implemented")
    }


     private suspend fun netRequestWrapper(logInfo: String, requestBody : suspend ()-> Unit){
        try {
            requestBody()
        } catch (e: ConnectException) {
            Log.e("netRequest", "ConnectException  ${e.message}  ${e.cause}")
            throw NetworkError
        } catch (e: SocketTimeoutException) {
                Log.e("netRequest", "SocketTimeoutException  ${e.message}  ${e.cause}")
            throw NetworkError
        } catch (e: ApiError) {
            Log.e("netRequest", "$logInfo ${e.javaClass.simpleName} ${e.status} | ${e.code} | ${e.cause}")
            throw e
        } catch (e: Error404) {
            Log.e("netRequest", "$logInfo ${e.javaClass.simpleName} 404 or Empty ")
            throw e
        } catch (e: IOException) {
            Log.e("netRequest", "$logInfo ${e.javaClass.simpleName} Unknown NetworkError ${e.message}  ${e.cause}")
            throw NetworkError
        } catch (e: Exception) {
            Log.e("netRequest", "$logInfo ${e.javaClass.simpleName} ${e.message}  ${e.cause}")
            throw UnknownError
        }
    }

    override suspend fun getJobsById(id: Long) {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getJobs(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            if (body.isEmpty()) {
                throw Error404
            }

            base.jobDao().insert(body.toEntity(id))
        }
    }

    override suspend fun postNewJob(job: NewJob) {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
          api.postNewJob(job)
        }
    }


    override suspend fun getEventbyId(id: Long) {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getEventById(id)
            if (!response.isSuccessful) {
                if (response.code() == 404) {
                    throw Error404
                }
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            base.eventDao().insert(EventEntity.fromDto(body))
        }
    }


    override suspend fun getWallbyId(id: Long) {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getWall(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            if (body.isEmpty()) {
                throw Error404
            }
            base.postDao().insert(body.toEntity())
        }
    }


    override suspend fun getAllEvents() {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getAllEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            base.eventDao().insert(body.toEntity())
        }
    }


    override suspend fun getAllUsers() {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getAllUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            base.userDao().insert(body.toEntity())
        }
    }


    override suspend fun getAllPosts() {
        netRequestWrapper(object{}.javaClass.enclosingMethod.name) {
            val response = api.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (body.isEmpty()) {
                throw Error404
            }
            base.postDao().insert(body.toEntity())
        }
    }


    //---------------- Todo: Refactor code below -------

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        /*  while (true) {
              delay(120_000L)
              val response = api.getNewer(id)
              if (!response.isSuccessful) {
                  throw ApiError(response.code(), response.message())
              }

              val body = response.body() ?: throw ApiError(response.code(), response.message())
              base.postDao().insert(body.toEntity())
              emit(body.size)
          }*/
        emit(0)
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)


    override suspend fun save(post: Post2) {
        try {
            val response = api.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            base.postDao().insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    // ---------------

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun likeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun saveWithAttachment(post: Post2, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE.toString()))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = api.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWork(post: Post2, upload: MediaUpload?): Long {
        /* try {
             val entity = PostWorkEntity.fromDto(post).apply {
                 if (upload != null) {
                     this.uri = upload.file.toUri().toString()
                 }
             }
             return base.postWorkDao().insert(entity)
         } catch (e: Exception) {
             throw UnknownError
         }*/
        return 0
    }

    override suspend fun processWork(id: Long) {
        /*try {
            // HOMEWORK
            Log.e("exc", "i am in processWork id=  ${ id}")
            val entity = base.postWorkDao().getById(id) ?: throw ru.netology.diploma.error.DbError
            Log.e("exc", "i am in processWork next  entity= ${entity}")
            if (entity.uri != null) {
                val upload = MediaUpload(Uri.parse(entity.uri).toFile())

                val post = Post2(
                    id = entity.id,
                    authorId = entity.authorId.toInt(),
                    content = entity.content,
                    likedByMe = entity.likedByMe,
                    authorAvatar = entity.authorAvatar,
                    author = entity.author,
                    published = entity.published
                )


                Log.e("exc", "i am in processWork next  saveWithAttachment")
                saveWithAttachment(post, upload)

              //  postWorkDao.removeById(id)
                Log.e("exc", "processWork DONE")

            }
            ///////////////////////////////////////
        } catch (e: Exception) {
            throw UnknownError
        }*/
    }
}
