package ru.kot1.demo.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.kot1.demo.auth.AppAuth
import ru.kot1.demo.model.*
import ru.kot1.demo.repository.AppEntities
import javax.inject.Inject




//Это вьюмодел заинжекчена без конструктора. даггер сам ее создает
@HiltViewModel
@ExperimentalCoroutinesApi
class EventViewModel @Inject constructor(var repository: AppEntities ,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {

    private val cachedevents = repository.edata.cachedIn(viewModelScope)

    private val _dataState = SingleLiveEvent<FeedModelState>()
    val dataState: SingleLiveEvent<FeedModelState>
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
             repository.getEventById(id)
            _dataState.value = FeedModelState()
        }

        catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshEvents (){
        getEventById(authorID)
    }

}




