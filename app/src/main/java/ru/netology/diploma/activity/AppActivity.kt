package ru.netology.diploma.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.activity.apppages.EventsFragment
import ru.netology.diploma.activity.apppages.JobFragment
import ru.netology.diploma.activity.apppages.NewJobFragment
import ru.netology.diploma.activity.apppages.PostsFragment
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    override fun  onBackPressed(){
        setTitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar((findViewById(R.id.toolbar)))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<LoadingFragment>(R.id.fragment_container_view, "loading")
            }
        } else {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<MainFragment>(R.id.fragment_container_view)
            }
        }

        viewModel.authData.observe(this) {
            invalidateOptionsMenu()
        }


        with(supportFragmentManager) {
            setFragmentResultListener("keyMainFragment", this@AppActivity) { _, bundle ->
                replaceFragment(MainFragment::class.java, bundle)
            }

            setFragmentResultListener("keyEvents", this@AppActivity) { _, bundle ->
                replaceFragment(EventsFragment::class.java, bundle)
            }

            setFragmentResultListener("keyWall", this@AppActivity) { _, bundle ->
                replaceFragment(PostsFragment::class.java, bundle)
            }

            setFragmentResultListener("keyJobs", this@AppActivity) { _, bundle ->
                replaceFragment(JobFragment::class.java, bundle)
            }

            setFragmentResultListener("keyNewJob", this@AppActivity) { _, bundle ->
                replaceFragment(NewJobFragment::class.java, bundle)
            }

            setFragmentResultListener("keyNewPost", this@AppActivity) { _, bundle ->
                replaceFragment(NewPostFragment::class.java, bundle)
            }
        }

    }

    private fun replaceFragment(
        java: Class<out Fragment>,
        bundle: Bundle
    ) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_container_view, java, bundle
            )
            addToBackStack(java.simpleName)
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
            android.R.id.home ->{
                    onBackPressed()
                  true
         }

            R.id.signin -> {
                showLoginAuthDialog(Dialog.LOGIN) { login, password, _ ->
                    appAuth.authUser(login, password) {
                        showFailDialog()
                    }
                }
                true
            }

            R.id.signup -> {
                showLoginAuthDialog(Dialog.REGISTER) { login, password, name ->
                    appAuth.regNewUserWithoutAvatar(login, password, name) {
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
}

