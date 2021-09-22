package ru.netology.diploma.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.entity.UserEntity
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

    private var myId : Long = -1

    val cachedposts = repository.pdata.cachedIn(viewModelScope)
    val cachedjobs = repository.jdata.cachedIn(viewModelScope)



    val feedPosts = cachedposts.map { pagingData ->
            pagingData.filter { post ->
                   post.authorId.toLong() == myId
                }.map {
                PostModel(post = it) as FeedModel
            }
        }


    val jobs = cachedjobs.map { pagingData ->
        pagingData.filter { job ->
            job.authorId == myId
        }
    }




    private val _postsDataState = MutableLiveData<FeedModelState>()
    val postsDataState: LiveData<FeedModelState>
        get() = _postsDataState

    private val _jobsDataState = MutableLiveData<FeedModelState>()
    val jobsDataState: LiveData<FeedModelState>
        get() = _jobsDataState


    var uid: MutableLiveData<Long> = MutableLiveData<Long>()
    private val _myInfoDataState = uid.switchMap { id ->
        repository.getUser(id)
    }

    val myInfoDataState: LiveData<List<UserEntity>>
        get() = _myInfoDataState



    //TO DO tab
    private val _toDoItemsCount = MutableLiveData<Int>(0)
    val toDoItemsCount: MutableLiveData<Int>
        get() = _toDoItemsCount

   fun loadContent(id: Long){
       myId = id
       uid.value = id
       loadMyJobs()
       loadMyPosts()
      // loadUsers()
   }

    fun loadContent(){
        loadContent(myId)
    }

    private fun loadMyPosts() = viewModelScope.launch {
        try {
            _postsDataState.value = FeedModelState(loading = true)
            repository.getAllPosts()
            _postsDataState.value = FeedModelState()
        }   catch (e: Exception) {
            _postsDataState.value = FeedModelState(error = true)
        }
    }


    private fun loadUsers() = viewModelScope.launch {
        try {
            _postsDataState.value = FeedModelState(loading = true)
            repository.getAllPosts()
            _postsDataState.value = FeedModelState()
        }   catch (e: Exception) {
            _postsDataState.value = FeedModelState(error = true)
        }
    }


    private fun loadMyJobs() = viewModelScope.launch {
        try {
            _jobsDataState.value = FeedModelState(loading = true)
            repository.getJobsById(myId)
            _jobsDataState.value = FeedModelState()
        }  catch (e: Exception) {
            _jobsDataState.value = FeedModelState(error = true)
        }
    }


}




