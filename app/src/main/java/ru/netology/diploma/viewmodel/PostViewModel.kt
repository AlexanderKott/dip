package ru.netology.diploma.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.MediaUpload
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.error.Error404
import ru.netology.diploma.model.*
import ru.netology.diploma.repository.AppEntities
import ru.netology.diploma.repository.PostRepository
import ru.netology.diploma.util.SingleLiveEvent
import ru.netology.diploma.work.SavePostWorker
import javax.inject.Inject
import kotlin.random.Random

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


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    fun getWallById(id: Long) = viewModelScope.launch {
        postAuthorId = id

        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getWallbyId(id)
            _dataState.value = FeedModelState()

        } catch (e: Error404) {
                _dataState.value = FeedModelState(empty = true)
                Log.e("OkHttpClient", "ApiError 404")

        } catch (e: Exception) {
            Log.e("OkHttpClient", "execption ${e.cause}  ${e.message}")
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun refreshPosts() = viewModelScope.launch {
        getWallById(postAuthorId)
    }
}




