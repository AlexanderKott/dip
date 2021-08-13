package ru.netology.diploma.repository

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
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.entity.UserEntity
import ru.netology.diploma.entity.toEntity
import ru.netology.diploma.enumeration.AttachmentType
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.error.AppError
import ru.netology.diploma.error.NetworkError
import ru.netology.diploma.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(
    private val base: AppDb,
    private val api: ApiService
) : AppEntities {


    @ExperimentalPagingApi
    override val pdata: Flow<PagingData<Post2>> = Pager(
        remoteMediator = PostRemoteMediator (api, base),
        config = PagingConfig( pageSize = 5 ,enablePlaceholders = false),
        pagingSourceFactory =  base.postDao()::getAll
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    @ExperimentalPagingApi
    override val udata: Flow<PagingData<User>> = Pager(
        remoteMediator = UserRemoteMediator (api, base),
        config = PagingConfig( pageSize = 5 ,enablePlaceholders = false),
        pagingSourceFactory =  base.userDao()::getAll
    ).flow.map {
        it.map(UserEntity::toDto)
    }

    @ExperimentalPagingApi
    override val edata: Flow<PagingData<Event>> = Pager(
        remoteMediator = EventsRemoteMediator (api, base),
        config = PagingConfig( pageSize = 5 ,enablePlaceholders = false),
        pagingSourceFactory =  base.eventDao()::getAll
    ).flow.map {
        it.map(EventEntity::toDto)
    }



    override suspend fun getAllEvents() {
        Log.e("ssss", "getAllEvents")
        try {
            val response = api.getAllEvents()
            if (!response.isSuccessful) {
                Log.e("ssss", "getAllEvents error")
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            // Log.e("ssss", "body= ${response.body()}")

            base.eventDao().insert(body.toEntity())

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun getAllUsers() {
        Log.e("ssss", "getAllUsers")
        try {
            val response = api.getAllUsers()
            if (!response.isSuccessful) {
                Log.e("ssss", "getAllUsers error")
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
           // Log.e("ssss", "body= ${response.body()}")

          base.userDao().insert(body.toEntity())

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw e
        }
    }



    //---------------------------------------------
    override suspend fun getAll() {
        try {
            val response = api.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            Log.e("OkHttpClient", "getAll body $body")
            base.postDao().insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            Log.e("OkHttpClient", "getAll Exception  ${e.message}  ${e.cause}")
            throw UnknownError
        }
    }

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
