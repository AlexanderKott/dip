package ru.kot1.demo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.kot1.demo.auth.AppAuth
import ru.kot1.demo.dto.Post
import ru.kot1.demo.model.FeedModel
import ru.kot1.demo.model.FeedModelState
import ru.kot1.demo.model.PostModel
import ru.kot1.demo.model.SingleLiveEvent
import ru.kot1.demo.repository.AppEntities
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class PostAllViewModel @Inject constructor(
    var repository: AppEntities,
    var workManager: WorkManager,
    var auth: AppAuth
) : ViewModel() {

    val cachedposts = repository.pdata.cachedIn(viewModelScope)

    /*  val feedModels = cachedposts.map { pagingData ->
          pagingData.map { post ->
              PostModel(post = post) as FeedModel
          }
      }*/

    val feedModels = auth.authStateFlow.flatMapLatest { logined ->
        cachedposts.map { pagingData ->
            pagingData.map { post ->
                PostModel(post = post.copy(logined = logined.id != 0L)) as FeedModel
            }
        }
    }


    private val _dataState = SingleLiveEvent<FeedModelState>()
    val dataState: SingleLiveEvent<FeedModelState>
        get() = _dataState


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAllPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            Log.e("OkHttpClient", "execption ${e.cause}  ${e.message}")
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun setLikeOrDislike(post: Post) = viewModelScope.launch {
        if (post.likedByMe) {
            repository.disLikeById(post.id)
        } else {
            repository.likeById(post.id)
        }
    }

}




