package ru.netology.diploma.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dto.AuthState
import ru.netology.diploma.dto.PushToken
import ru.netology.diploma.repository.AuthMethods
import javax.inject.Inject


class AppAuth @Inject constructor(
    val context: Context,
    val apiService: ApiService,
    var repository: AuthMethods
) {
    companion object {
        private lateinit var prefs: SharedPreferences
          const val idKey = "id"
          const val tokenKey = "token"

        fun getAuthInfo(context: Context): Pair<Long, String?> {
            prefs = context.getSharedPreferences("authX", Context.MODE_PRIVATE)
            return prefs.getLong(idKey, 0) to
                    prefs.getString(tokenKey, null)
        }
    }



    private val _authStateFlow: MutableStateFlow<AuthState> = MutableStateFlow(AuthState())


    init {
        val (id, token) = getAuthInfo(context)

        if (id == 0L || token == null) {  //ничего нет- чистим
            cleanToken()
            Log.e("OkHttpClient", "no token , nothing to do")
            // токена нет - ничего не делать

        } else {
            Log.e("OkHttpClient", " checkToken  Start checkTheToken")

            // проверить что ключ валидный
            checkTheToken ({
                //оказался валидный? - присваиваем
                _authStateFlow.value = AuthState(id, token)
                Log.e("OkHttpClient", " checkToken token valid END")
            }, {
                cleanToken()
                Log.e("OkHttpClient", " checkToken token FAIL END")
            })

        }


        //sendPushToken()
    }



    private fun cleanToken() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            apply()
        }
    }

    private fun checkTheToken(success: () -> Unit, failure : () -> Unit ) = CoroutineScope(Dispatchers.Default).launch {
        if (repository.checkToken()) {
            success()
        } else {
            failure ()
        }
    }


    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()


    //----------------------
    fun authUser(login: String, pass: String, failCase : ()-> Unit) = CoroutineScope(Dispatchers.Default).launch {
        cleanToken()
        try {
            repository.authUser(login, pass) { id, token ->
                setAuth(id, token)
            }
        }   catch (e: Exception) {
            failCase()
            Log.e("OkHttpClient", " testAuth execption ${e.cause}  ${e.message}")
        }
    }


    fun regNewUserWithoutAvatar(login: String, pass: String, name: String, failCase : ()-> Unit) =
        CoroutineScope(Dispatchers.Default).launch {
            try {
                repository.regNewUserWithoutAvatar(login, pass, name){ id, token ->
                    setAuth(id, token)
                }

            } catch (e: Exception) {
                failCase()
                Log.e("OkHttpClient", " testRegWithoutAvatar execption ${e.cause}  ${e.message}")
            }
        }


    //-------


    @Synchronized
    private fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            apply()
        }
    }


}

