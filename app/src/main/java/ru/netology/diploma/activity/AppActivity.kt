package ru.netology.diploma.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.activity.NewPostFragment.Companion.textArg
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity: AppCompatActivity (R.layout.activity_app) {
    private val viewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var fb: FirebaseInstallations
    private lateinit var fbm: FirebaseMessaging

    @Inject lateinit var appAuth : AppAuth
    @Inject lateinit var gava: GoogleApiAvailability

    @Inject
    fun setFirebaseInstallations(f: FirebaseInstallations){
        fb = f
    }

    @Inject
    fun setFirebaseMessaging(f: FirebaseMessaging){
        fbm = f
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }


        fb.id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }


        fbm.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                // TODO: just hardcode it, implementation must be in homework
                appAuth.setAuth(5, "x-token")
                Log.e("exc", "signin")
                true
            }
            R.id.signup -> {
                // TODO: just hardcode it, implementation must be in homework
                appAuth.setAuth(5, "x-token")
                true
            }
            R.id.signout -> {
                // TODO: just hardcode it, implementation must be in homework
                appAuth.removeAuth()
                postViewModel.refreshPosts()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun checkGoogleApiAvailability() {
        with(gava) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000).show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }
}