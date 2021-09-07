package ru.netology.diploma.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(var auth: AppAuth) : ViewModel() {

    val authData: LiveData<AuthState> = auth
        .authStateFlow
        .asLiveData(Dispatchers.Default)



    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L
}