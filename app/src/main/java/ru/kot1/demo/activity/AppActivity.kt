package ru.kot1.demo.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import ru.kot1.demo.R
import ru.kot1.demo.activity.pages.EventsFragment
import ru.kot1.demo.activity.pages.JobFragment
import ru.kot1.demo.activity.editors.NewJobFragment
import ru.kot1.demo.activity.editors.NewPostFragment
import ru.kot1.demo.activity.pages.PostsFragment
import ru.kot1.demo.activity.utils.Dialog
import ru.kot1.demo.activity.utils.showAuthDialog
import ru.kot1.demo.activity.utils.showLoginAuthDialog
import ru.kot1.demo.auth.AppAuth
import ru.kot1.demo.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    override fun  onBackPressed(){
        setTitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (findViewById<Toolbar>(R.id.toolbar)).setNavigationIcon(R.drawable.ic_baseline_menu_book_24)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar((findViewById(R.id.toolbar)))
        (findViewById<Toolbar>(R.id.toolbar)).setNavigationIcon(R.drawable.ic_baseline_menu_book_24)

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
                    viewModel.selectMyPage()
                    appAuth.authUser(login, password) {
                        showAuthDialog(it)
                        viewModel.markMyPageAlreadyOpened()
                    }
                }
                true
            }

            R.id.signup -> {
                showLoginAuthDialog(Dialog.REGISTER) { login, password, name ->
                    viewModel.selectMyPage()
                    appAuth.regNewUserWithoutAvatar(login, password, name) {
                        showAuthDialog(it)
                        viewModel.markMyPageAlreadyOpened()
                    }
                }
                true
            }

            R.id.signout -> {
                appAuth.removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

