package ru.netology.diploma.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.error.Error404
import ru.netology.diploma.model.*
import ru.netology.diploma.repository.AppEntities
import javax.inject.Inject


private val noPhoto = PhotoModel()

@HiltViewModel
@ExperimentalCoroutinesApi
class EventAllViewModel @Inject constructor(var repository: AppEntities ,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {

    val cachedevents = repository.edata.cachedIn(viewModelScope)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    init {
        loadEvents()
    }


    fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
                repository.getAllEvents()
            _dataState.value = FeedModelState()
        }
        catch (e: Error404) {
            _dataState.value = FeedModelState(empty = true)
            }


        catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }








}




