package ru.netology.diploma.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.diploma.dto.*


interface ApiService {

    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>


    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("posts")
    suspend fun getAll(): Response<List<Post2>>

    //save
    @POST("posts")
    suspend fun save(@Body post: Post2): Response<Post2>


    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post2>>

    @GET("posts/latest")
    suspend fun getLatestPosts(
        @Query("count") count: Int
    ): Response<List<Post2>>

    //-----------------------


    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>





    @GET("posts/{id}/before")
    suspend fun getBeforePosts(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfterPosts(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>




    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>



    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

}