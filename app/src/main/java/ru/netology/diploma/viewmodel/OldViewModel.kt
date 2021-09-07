package ru.netology.diploma.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.MediaUpload
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.error.Error404
import ru.netology.diploma.model.*
import ru.netology.diploma.repository.AppEntities
import ru.netology.diploma.util.SingleLiveEvent
import ru.netology.diploma.work.SavePostWorker
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

private val noPhoto = PhotoModel()

//Это вьюмодел заинжекчена без конструктора. даггер сам ее создает
@HiltViewModel
@ExperimentalCoroutinesApi
class OldViewModel @Inject constructor(var repository: AppEntities ,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {
    private var postAuthorId : Long = -1

    val cachedposts = repository.pdata.cachedIn(viewModelScope)
    val cachedusers = repository.udata.cachedIn(viewModelScope)
    val cachedevents = repository.edata.cachedIn(viewModelScope)



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





    /*  val dataPosts: Flow<PagingData<FeedModel>> = auth.authStateFlow
          .flatMapLatest { (myId, _) ->
              cachedposts.map { pagingData ->
                  pagingData.map { post ->
                      PostModel(post = post.copy(ownedByMe = post.authorId == myId))
                      }.insertSeparators { postModel: FeedModel?, postModel2: FeedModel? ->
                      if (postModel?.id?.rem(5)  == 0L){
                          AdModel(Random.nextLong(), "figma.jpg")
                      } else {
                          null
                      }

                  }
             }
          }*/





    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
        getEventById(-1)
        loadUsers()
    }

    fun loadEvents(){
        getEventById(-1)
    }
    fun getEventById(){
        getEventById(postAuthorId)
    }







    fun getEventById(id: Long) = viewModelScope.launch {
        postAuthorId = id

        try {
            _dataState.value = FeedModelState(loading = true)
            if (id == -1L) {
                repository.getAllEvents()
            } else {
             repository.getEventbyId(id)
            }
            _dataState.value = FeedModelState()
        }
        catch (e: Error404) {
            _dataState.value = FeedModelState(empty = true)
                Log.e("OkHttpClient", "ApiError 404")
            }


        catch (e: Exception) {
            Log.e("OkHttpClient", "execption ${e.cause}  ${e.message}")
            _dataState.value = FeedModelState(error = true)
        }
    }



    fun getWallById(id: Long) = viewModelScope.launch {
        postAuthorId = id

        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getWallbyId(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            Log.e("OkHttpClient", "execption ${e.cause}  ${e.message}")
            _dataState.value = FeedModelState(error = true)
        }
    }

 /*   fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAllEvents()
            _dataState.value = FeedModelState()
        } catch (e: ApiError) {

            _dataState.value = FeedModelState(error = true)
            Log.e("ssss", "loadEvents ApiError ${e.code} ${e.message}")

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
            Log.e("ssss", "loadEvents Exception= ${e.message}  ${e.cause} ${e.stackTrace.toString()} ")
        }
    }*/


    fun loadUsers() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAllUsers()
            _dataState.value = FeedModelState()
        } catch (e: ApiError) {

            _dataState.value = FeedModelState(error = true)
            Log.e("ssss", "loadUsers ApiError ${e.code} ${e.message}")

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
            Log.e("ssss", "loadUsers Exception= ${e.message}  ${e.cause} ${e.stackTrace.toString()} ")
        }
    }



    //---------------------------------------------------------

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
        postAuthorId = -1
        try {
            _dataState.value = FeedModelState(refreshing = true)
             repository.getAllPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post2) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun likeById(id: Long) {

    }

    fun removeById(id: Long) {
    }



}




