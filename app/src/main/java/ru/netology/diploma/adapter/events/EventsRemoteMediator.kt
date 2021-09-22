package ru.netology.diploma.adapter.events

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.db.AppDb
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.EventKeyEntry
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.repository.AppNetState
import ru.netology.diploma.repository.AuthMethods


@ExperimentalPagingApi
class EventsRemoteMediator(private val api: ApiService,
                           private val base: AppDb,
                           private val repoNetwork: AuthMethods
)
    : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
            ): MediatorResult {
        try {
            val connected = repoNetwork.checkConnection() == AppNetState.CONNECTION_ESTABLISHED

            if (connected) {
            val response = when (loadType){
                else -> {
                    base.eventDao().deleteAll()
                    //api.getLatestPosts(state.config.pageSize)
                    api.getAllEvents()
                }
               /*   LoadType.REFRESH -> {
                      base.postDao().deleteAll()
                      api.getLatestPosts(state.config.pageSize)
                  }
                  LoadType.APPEND -> {
                      val id = base.keyWorkDao().min() ?: return MediatorResult.Success(false)
                     // api.getBeforePosts(id, state.config.pageSize)
                  }
                  LoadType.PREPEND -> {
                      val id = base.keyWorkDao().max() ?: return MediatorResult.Success(false)
                    //  api.getAfterPosts(id, state.config.pageSize)
                  }*/
            }

           if (! response.isSuccessful){
               Log.e("OkHttpClient", "remote mediator ApiError")
               throw ApiError(response.code(), response.message())
           }

            val body = response.body() ?: throw  ApiError(
                response.code(),
                response.message()
            )

            if (body.isEmpty()){
                Log.e("OkHttpClient", "remote mediator success")
                return MediatorResult.Success(true)

            }

            base.withTransaction {
                when (loadType){
                    LoadType.REFRESH -> {
                        base.keyEventPaginationDao().insert(
                            listOf(
                                 EventKeyEntry(
                                     EventKeyEntry.Type.PREPEND,
                                     body.first().id.toLong()
                                 ),
                                EventKeyEntry(
                                    EventKeyEntry.Type.APPEND,
                                    body.last().id.toLong()
                                )
                            )
                        )
                        base.eventDao().deleteAll()
                    }
                    LoadType.PREPEND -> {
                        base.keyEventPaginationDao().insert(
                            listOf(
                                EventKeyEntry(
                                    EventKeyEntry.Type.PREPEND,
                                    body.first().id.toLong()
                                ),
                            )
                        )

                    }
                    LoadType.APPEND ->  {
                        base.keyEventPaginationDao().insert(
                            listOf(
                                EventKeyEntry(
                                    EventKeyEntry.Type.APPEND,
                                    body.last().id.toLong()
                                )
                            )
                        )

                    }
                }

                base.eventDao().insert(body.map(EventEntity.Companion::fromDto))
            }



            return  MediatorResult.Success(true)
            } else {
                return MediatorResult.Success(true)
            }
        } catch (e: Exception){
            Log.e("OkHttpClient", "remote mediator Exception")
           return MediatorResult.Error(e)
        }
    }
}