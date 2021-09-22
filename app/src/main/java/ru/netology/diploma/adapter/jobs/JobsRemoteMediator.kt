package ru.netology.diploma.adapter.jobs

import android.content.Context
import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.db.AppDb
import ru.netology.diploma.entity.JobEntity
import ru.netology.diploma.entity.toEntity
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.repository.AppNetState
import ru.netology.diploma.repository.AuthMethods

@ExperimentalPagingApi
class JobsRemoteMediator(private val api: ApiService,
                         private val context: Context,
                         private val base: AppDb,
                         private val repoNetwork: AuthMethods
)
    : RemoteMediator<Int, JobEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, JobEntity>
            ): MediatorResult {
        try {
            val connected = repoNetwork.checkConnection() == AppNetState.CONNECTION_ESTABLISHED

            if (connected) {
           // val id = AppAuth.getAuthInfo(context).first
                Log.e("ssss", "job RemoteMediator id=  {id}")

          /*  val response = when (loadType){
                else -> {
                    base.jobDao().deleteAll()
                    //api.getLatestPosts(state.config.pageSize)
                    api.getJobs(3)
                }
               *//*   LoadType.REFRESH -> {
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
                  }*//*
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



                base.jobDao().insert(body.toEntity(3))
*/


                Log.e("OkHttpClient", "connected END")
            return  MediatorResult.Success(true)
        } else {
                Log.e("OkHttpClient", "DIS connected END")
            return MediatorResult.Success(true)
        }
        } catch (e: Exception){
            Log.e("OkHttpClient", "remote mediator Exception")
           return MediatorResult.Error(e)
        }
    }
}