package ru.netology.diploma.adapter.users

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.db.AppDb
import ru.netology.diploma.entity.UserEntity
import ru.netology.diploma.entity.UserKeyEntry
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.repository.AppNetState
import ru.netology.diploma.repository.AuthMethods

@ExperimentalPagingApi
class UserRemoteMediator(private val api: ApiService,
                         private val base: AppDb,
                         private val repoNetwork: AuthMethods
)
    : RemoteMediator<Int, UserEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
            ): MediatorResult {

        try {
            val connected = repoNetwork.checkConnection() == AppNetState.CONNECTION_ESTABLISHED

            if (connected) {
                val response = when (loadType) {
                    else -> {
                        Log.e("ssss", "users refresh!!!")

                        base.userDao().deleteAll()
                        api.getAllUsers()
                    }
                }

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }

                val body = response.body() ?: throw  ApiError(
                    response.code(),
                    response.message()
                )

                if (body.isEmpty()) {
                    return MediatorResult.Success(true)
                }

                base.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            base.keyUserPaginationDao().insert(
                                listOf(
                                    UserKeyEntry(
                                        UserKeyEntry.Type.PREPEND,
                                        body.first().id
                                    ),
                                    UserKeyEntry(
                                        UserKeyEntry.Type.APPEND,
                                        body.last().id
                                    )
                                )
                            )
                            base.userDao().deleteAll()
                        }
                        LoadType.PREPEND -> {
                            base.keyUserPaginationDao().insert(
                                listOf(
                                    UserKeyEntry(
                                        UserKeyEntry.Type.PREPEND,
                                        body.first().id
                                    ),
                                )
                            )

                        }
                        LoadType.APPEND -> {
                            base.keyUserPaginationDao().insert(
                                listOf(
                                    UserKeyEntry(
                                        UserKeyEntry.Type.APPEND,
                                        body.last().id
                                    )
                                )
                            )

                        }
                    }

                    base.userDao().insert(body.map(UserEntity.Companion::fromDto))
                }



                return MediatorResult.Success(true)
            } else {
                return MediatorResult.Success(true)
            }
        } catch (e: Exception){
           return MediatorResult.Error(e)
        }
    }
}