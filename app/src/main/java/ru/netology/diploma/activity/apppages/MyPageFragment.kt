package ru.netology.diploma.activity.apppages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.BuildConfig
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.jobs.JobAdapter
import ru.netology.diploma.adapter.jobs.OnJobsInteractionListener
import ru.netology.diploma.adapter.posts.OnInteractionListener
import ru.netology.diploma.adapter.posts.PostsAdapter
import ru.netology.diploma.databinding.FragmentMyPageBinding
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.view.load
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.MyPageViewModel

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private val viewModel: MyPageViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postsAdapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post2) {
            }

            override fun onLike(post: Post2) {
            }

            override fun onRemove(post: Post2) {
            }

            override fun onAdClick(ad: AdModel) {
            }

            override fun onShare(post: Post2) {
            }
        })


        binding.postLst.adapter = postsAdapter
            .withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(postsAdapter::retry),
                footer = PagingLoadStateAdapter(postsAdapter::retry)
            )


        binding.postLst.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )


        val jobsAdapter = JobAdapter(object : OnJobsInteractionListener {
            override fun onJobClick(job: Job) {
                Toast.makeText(requireContext(), "Demo-version", Toast.LENGTH_SHORT).show()
            }

            override fun onJobRemove(job: Job) {
                Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
            }
        })


        binding.jobLst.adapter = jobsAdapter
            .withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(postsAdapter::retry),
                footer = PagingLoadStateAdapter(postsAdapter::retry)
            )



        authViewModel.authData.observe(viewLifecycleOwner) { data ->
            if (data.token != null) {
                viewModel.loadContent(data.id)
                binding.groupLogined.isVisible = true
                binding.groupNotLogined.isVisible = false
            } else {
                binding.groupLogined.isVisible = false
                binding.groupNotLogined.isVisible = true
            }
        }


        viewModel.postsDataState.observe(viewLifecycleOwner) { state ->
            binding.progressWall.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadContent() }
                    .show()
            }
        }


        viewModel.jobsDataState.observe(viewLifecycleOwner) { state ->
            binding.progressJobs.isVisible = state.loading
        }

        viewModel.myInfoDataState.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                binding.username.text = "Мое имя: ${users[0].name}"
                binding.ava.load("${BuildConfig.BASE_URL}/avatars/${users[0].avatar}")
            }
        }



        lifecycleScope.launchWhenCreated {
            viewModel.feedPosts.collectLatest {
                postsAdapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.jobs.collectLatest {
                jobsAdapter.submitData(it)
            }
        }

        postsAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh.endOfPaginationReached) {
                if (binding.groupLogined.isVisible)
                    if (postsAdapter.itemCount == 0) {
                        binding.noPostsL.isVisible = true
                        binding.postLst.isVisible = false
                    } else {
                        binding.noPostsL.isVisible = false
                        binding.postLst.isVisible = true
                    }
            }
        }


        jobsAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh.endOfPaginationReached) {
                if (binding.groupLogined.isVisible)
                    if (jobsAdapter.itemCount == 0) {
                        binding.nojobsL.isVisible = true
                        binding.jobLst.isVisible = false
                    } else {
                        binding.nojobsL.isGone = true
                        binding.jobLst.isVisible = true
                    }
            }
        }


        lifecycleScope.launchWhenCreated {
            postsAdapter.loadStateFlow.collectLatest { states ->
                binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading

            }
        }


        binding.newJob.setOnClickListener {
            setFragmentResult("keyNewJob", Bundle())
        }

        binding.newPost.setOnClickListener {
            setFragmentResult("keyNewPost", Bundle())
        }


    }
}
