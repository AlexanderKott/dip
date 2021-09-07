package ru.netology.diploma.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.activity.apppages.EventsFragment
import ru.netology.diploma.activity.apppages.JobFragment
import ru.netology.diploma.activity.apppages.NewJobFragment
import ru.netology.diploma.activity.apppages.PostsFragment
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.OldViewModel
import javax.inject.Inject


@AndroidEntryPoint
class AppActivity: AppCompatActivity (R.layout.activity_app) {
    private val viewModel: AuthViewModel by viewModels()
    private val oldViewModel: OldViewModel by viewModels()
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

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MainFragment>(R.id.fragment_container_view)
        }

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)

        }

        viewModel.authData.observe(this) {
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



        supportFragmentManager
            .setFragmentResultListener("keyEvents", this) { _, bundle ->
                Log.e("ssss", "supportFragmentManager key1 END")
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,  EventsFragment::class.java, bundle
                    )
                    addToBackStack("keyEvents")
                }
            }

        supportFragmentManager
            .setFragmentResultListener("keyWall", this) { _, bundle ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,  PostsFragment::class.java, bundle
                    )
                    addToBackStack("keyWall")
                }
            }

        supportFragmentManager
            .setFragmentResultListener("keyJobs", this) { _, bundle ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,  JobFragment::class.java, bundle
                    )
                    addToBackStack("keyJobs")
                }
            }

        supportFragmentManager
            .setFragmentResultListener("keyNewJob", this) { _, bundle ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,  NewJobFragment::class.java, bundle
                    )
                    addToBackStack("keyNewJob")
                }
            }


        supportFragmentManager
            .setFragmentResultListener("keyNewPost", this) { _, bundle ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,  NewPostFragment::class.java, bundle
                    )
                    addToBackStack("keyNewPost")
                }
            }




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
                showLoginAuthDialog(Dialog.LOGIN) { login, password, _ ->
                    appAuth.authUser(login, password){
                        showFailDialog()
                    }
                }
                true
            }

            R.id.signup -> {
                showLoginAuthDialog(Dialog.REGISTER) { login, password, name ->
                    appAuth.regNewUserWithoutAvatar(login, password, name){
                        showFailDialog()
                    }
                }
                true
            }

            R.id.signout -> {
                appAuth.removeAuth()
                //todo Refresh
               /// postViewModel.refreshPosts()
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