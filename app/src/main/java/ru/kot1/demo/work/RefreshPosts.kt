package ru.kot1.demo.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kot1.demo.repository.AppEntities

@HiltWorker
class RefreshPostsWorker @AssistedInject constructor(
    @Assisted  applicationContext: Context,
    @Assisted  params: WorkerParameters,
    var repository:  AppEntities
    ) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.netology.work.RefreshPostsWorker"
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getAllPosts()
            Result.success() //Воркер уведомляет тех кому это интрерсно (кто на него подписан)
                              // что результат успешен
                              //на воркер можно подписаться так же как и на лайв дату
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()   //Уведомить андроид ос, что метод надо повторить. Андроид сам выберет когда
        }
    }
}