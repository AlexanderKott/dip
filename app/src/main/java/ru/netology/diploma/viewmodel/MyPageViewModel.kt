package ru.netology.diploma.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.error.Error404
import ru.netology.diploma.model.FeedModel
import ru.netology.diploma.model.FeedModelState
import ru.netology.diploma.model.PostModel
import ru.netology.diploma.repository.AppEntities
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class MyPageViewModel @Inject constructor(var repository: AppEntities,
                                          var workManager: WorkManager,
                                          var auth: AppAuth
) : ViewModel() {

    init {
        Log.e("lll", "MyPageViewModel test")

    }
    private var myId : Long = -1

    val cachedposts = repository.pdata.cachedIn(viewModelScope)
    val cachedjobs = repository.jdata.cachedIn(viewModelScope)
    val cachedusers = repository.udata.cachedIn(viewModelScope)


    val myName = cachedusers.map { pagingData ->
        pagingData.filter { user ->
            user.id == myId
        }.map {
            it.name
        }.map { "it" }

    }


    val feedPosts = cachedposts.map { pagingData ->
            pagingData.filter { post ->
                   post.authorId.toLong() == myId
                }.map {
                PostModel(post = it) as FeedModel
            }
        }


    val jobs = cachedjobs.map { pagingData ->
        pagingData.filter { job ->
            Log.e("lll", "job.authorId ${job.authorId}")
            job.authorId == myId
        }
    }





    private val _postsDataState = MutableLiveData<FeedModelState>()
    val postsDataState: LiveData<FeedModelState>
        get() = _postsDataState


    private val _jobsDataState = MutableLiveData<FeedModelState>()
    val jobsDataState: LiveData<FeedModelState>
        get() = _jobsDataState

   fun loadContent(id: Long){
       myId = id

       loadMyJobs()
       loadMyPosts()

   }

    private fun loadMyPosts() = viewModelScope.launch {
        try {
            _postsDataState.value = FeedModelState(loading = true)
            repository.getAllPosts()
            _postsDataState.value = FeedModelState()

        }  catch (e: Error404) {
            _postsDataState.value = FeedModelState(empty = true)
                Log.e("OkHttpClient", "ApiError 404")


        } catch (e: Exception) {
            Log.e("OkHttpClient", "loadMyData execption ${e.cause}  ${e.message}")
            _postsDataState.value = FeedModelState(error = true)
        }
    }


    private fun loadMyJobs() = viewModelScope.launch {
        try {
            _jobsDataState.value = FeedModelState(loading = true)
            repository.getJobsById(myId)
            _jobsDataState.value = FeedModelState()

        }  catch (e: Error404) {
            _jobsDataState.value = FeedModelState(empty = true)

        } catch (e: Exception) {
            _jobsDataState.value = FeedModelState(error = true)
        }
    }


}




