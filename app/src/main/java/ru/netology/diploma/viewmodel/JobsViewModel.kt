package ru.netology.diploma.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.NewJob
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.model.FeedModelState
import ru.netology.diploma.model.SingleLiveEvent
import ru.netology.diploma.repository.AppEntities
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class JobsViewModel @Inject constructor(
    var repository: AppEntities,
    var workManager: WorkManager,
    var auth: AppAuth
) : ViewModel() {
    private val cachedjobs = repository.jdata.cachedIn(viewModelScope)


    private val _dataState = SingleLiveEvent<FeedModelState>()
    val dataState: SingleLiveEvent<FeedModelState>
        get() = _dataState

    private var usedId = 0L

    val jobs = cachedjobs.map { pagingData ->
        pagingData.filter { job ->
            Log.e("ssss", "job id= ${job.id}")
            job.authorId == usedId
        }
    }


    fun refreshJobs() {
        loadJobsById(usedId)
    }

    fun loadJobsById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            usedId = id
            repository.getJobsById(id)
            _dataState.value = FeedModelState()
        }  catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
            Log.e(
                "ssss",
                "loadJobsById Exception= ${e.message}  ${e.cause} ${e.stackTrace.toString()} "
            )
        }
    }

    fun postNewJob(cname: String, pos: String, l: Long, l1: Long) = viewModelScope.launch {
        try {
            repository.postNewJob(NewJob(position = pos, name = cname, finish = l, start = l1))

        } catch (e: Exception) {
        }

    }


}




