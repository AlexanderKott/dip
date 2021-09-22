package ru.netology.diploma.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.diploma.BuildConfig
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.db.AppDb
import ru.netology.diploma.repository.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton



@Module
@InstallIn(ViewModelComponent::class)
internal object ModuleForViewModel{

    @Provides
    fun getWorkManager(@ApplicationContext context: Context): WorkManager =
     WorkManager.getInstance(context)

}

@Module
@InstallIn(SingletonComponent::class)
internal object ModuleForSingleton {

    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
// /api/slow/"


    @Singleton
    @Provides
    fun getPostRepository(db: AppDb, api: ApiService, @ApplicationContext context: Context): AppEntities =
        PostRepositoryImpl(db, api, context)

    @Singleton
    @Provides
    fun getRepositoryInet(repo : AppEntities) = repo as AuthMethods



    @Provides
    fun getAppDb(@ApplicationContext context: Context) = AppDb.getInstance(context = context)


    @Singleton
    @Provides
    fun getAppAuth(@ApplicationContext context : Context, api : ApiService, repo : AppEntities): AppAuth {
       return AppAuth(context, api, repo)
    }


    @Provides
    fun getApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Provides
    fun getRetrofit(okhttp: OkHttpClient) = Retrofit.Builder()
         .baseUrl(BASE_URL)
        .client(okhttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

  @Provides
  fun getPrefs(@ApplicationContext context: Context) : SharedPreferences {
      return context.getSharedPreferences("authX", Context.MODE_PRIVATE)
  }

    @Provides
    fun getService(prefs : SharedPreferences)  = OkHttpClient.Builder()
        .connectTimeout(55, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            prefs.getString("token", null)?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()


    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {

            level = HttpLoggingInterceptor.Level.BODY
        }
    }






}
