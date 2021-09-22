package ru.netology.diploma.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.model.FeedModelState
import ru.netology.diploma.model.SingleLiveEvent
import ru.netology.diploma.repository.AppEntities
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class UsersViewModel @Inject constructor(var repository: AppEntities,
                                        var workManager: WorkManager,
                                        var auth: AppAuth
) : ViewModel() {
    val cachedusers = repository.udata.cachedIn(viewModelScope)

    private val _dataState = SingleLiveEvent<FeedModelState>()
    val dataState: SingleLiveEvent<FeedModelState>
        get() = _dataState

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
            Log.e("ssss", "loadUsers Exception=   ${e.javaClass.simpleName} ${e.message}  ${e.cause} ${e.stackTrace.toString()} ")
        }
    }




}




