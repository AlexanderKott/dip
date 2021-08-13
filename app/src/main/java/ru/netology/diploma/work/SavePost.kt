package ru.netology.diploma.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.netology.diploma.error.UnknownError
import ru.netology.diploma.repository.AppEntities
import ru.netology.diploma.repository.PostRepository

/**
 * Это переодичный воркер, андроид ос сам его заускает когда приходит время
 * выставленное при его инициализации
 */
@HiltWorker
class SavePostWorker  @AssistedInject constructor(
    @Assisted  applicationContext: Context,
    @Assisted  params: WorkerParameters,
    var repository: AppEntities
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "post"
    }

    ///Это класс воркера, овверайдим doWork  который выполняет рабоу в фоне
    override suspend fun doWork(): Result {
        //Получаем входные данные (могут быть тольк оримитивы и стринг)
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure() //если воркер не получил данных то он возвращает фейл и выходит
        }

        return try {
            Log.e("exc", "WORKER doWork for ${id}")
            repository.processWork(id)
            Log.e("exc", "WORKER DONE")
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        } catch (e: UnknownError) {
            Result.failure()
        }


    }
}
