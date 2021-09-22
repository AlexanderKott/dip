package ru.netology.diploma.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.*
import ru.netology.diploma.repository.AppEntities
import javax.inject.Inject

private val empty = Post2(
 attachment= null,
 author= "",
 authorAvatar= "",
 authorId= 0,
 content= "",
 coords= "",
 id= 0,
 likeOwnerIds= null,
 likedByMe= false,
 link= "",
 mentionIds= null,
 mentionedMe= false,
 published= 0
)


//Это вьюмодел заинжекчена без конструктора. даггер сам ее создает
@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(var repository: AppEntities ,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {
    private var postAuthorId : Long = -1

    val cachedposts = repository.pdata.cachedIn(viewModelScope)

    val feedModels = cachedposts.map { pagingData ->
        if (postAuthorId == -1L){
            pagingData.map { post ->
                    PostModel(post = post) as FeedModel
                }
        } else { pagingData.filter { postAuthorId == it.authorId.toLong()  }
            .map { post ->
                PostModel(post = post) as FeedModel
            }
        }
    }


    private val _dataState = SingleLiveEvent<FeedModelState>()
    val dataState: SingleLiveEvent<FeedModelState>
        get() = _dataState


    fun getWallById(id: Long) = viewModelScope.launch {
        postAuthorId = id
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getWallbyId(id)
            _dataState.value = FeedModelState()

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun refreshPosts() = viewModelScope.launch {
        getWallById(postAuthorId)
    }
}




