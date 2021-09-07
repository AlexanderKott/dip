package ru.netology.diploma.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.diploma.dto.*


interface ApiService {


    @POST("my/jobs")
    suspend fun postNewJob(@Body newJob: NewJob): Response<Unit>

    @GET("my/jobs")
    suspend fun getMyJobs(): Response<List<Job>>

    @POST("posts/-555/likes")
    suspend fun checkToken(): Response<Unit>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("users/authentication")
    suspend fun authMe (@Query("login") login: String,@Query("pass") pass: String)
    : Response<AuthState>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("users/registration")
    suspend fun regMeWithoutAvatar (@Query("login") login: String,
                                    @Query("pass") pass: String,
                                    @Query("name") name: String,
                                    )
            : Response<AuthState>





    @GET("{id}/jobs")
    suspend fun getJobs(@Path("id") id : Long): Response<List<Job>>

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


    @GET("{id}/wall")
    suspend fun getWall(@Path("id") id: Long): Response<List<Post2>>

    //-----------------------

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>



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