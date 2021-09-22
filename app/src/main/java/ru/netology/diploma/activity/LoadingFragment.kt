package ru.netology.diploma.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.databinding.FragmentLoadingBinding
import ru.netology.diploma.repository.AppNetState
import ru.netology.diploma.repository.AuthMethods
import ru.netology.diploma.viewmodel.EventAllViewModel
import ru.netology.diploma.viewmodel.PostAllViewModel
import ru.netology.diploma.viewmodel.UsersViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoadingFragment : Fragment() {
    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private val usersVM: UsersViewModel by activityViewModels()
    private val postsVM: PostAllViewModel by activityViewModels()
    private val eventsVM: EventAllViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var repoNetwork: AuthMethods

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?//
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            when (repoNetwork.checkConnection()) {
                AppNetState.NO_INTERNET -> displayError("No internet")
                AppNetState.NO_SERVER_CONNECTION -> displayError("Server is unavailable")
                AppNetState.CONNECTION_ESTABLISHED -> {
                    appAuth.checkAmLogined()
                    usersVM.loadUsers()
                    postsVM.loadPosts()
                    eventsVM.loadEvents()
                    setFragmentResult("keyMainFragment", Bundle())
                }
            }

        }

        binding.proceed.setOnClickListener {
            setFragmentResult("keyMainFragment", Bundle())
        }
    }

    private fun displayError(errorT: String) {
        val handler = Handler(Looper.getMainLooper());
        handler.post {
            with(binding) {
                groupError.isVisible = true
                errorL.text = errorT
                progressBar.isVisible = false
            }

        }
    }
}
