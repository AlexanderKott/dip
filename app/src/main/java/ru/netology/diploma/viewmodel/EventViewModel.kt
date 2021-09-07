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



private val noPhoto = PhotoModel()

//Это вьюмодел заинжекчена без конструктора. даггер сам ее создает
@HiltViewModel
@ExperimentalCoroutinesApi
class EventViewModel @Inject constructor(var repository: AppEntities ,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {

    private val cachedevents = repository.edata.cachedIn(viewModelScope)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    var authorID = 0L;


    val feedEvents = cachedevents.map { pagingData ->
        if (authorID == -1L){
            pagingData.map { event ->
                event
            }
        } else { pagingData.filter { authorID == it.authorId.toLong()  }
            .map { event ->
                event
            }
        }
    }


    fun getEventById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
             authorID = id;
             repository.getEventbyId(id)
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

    fun refresh (){
        getEventById(authorID)
    }

}




