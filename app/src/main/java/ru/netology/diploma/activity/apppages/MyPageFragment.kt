package ru.netology.diploma.activity.apppages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.jobs.JobAdapter
import ru.netology.diploma.adapter.jobs.OnJobsInteractionListener
import ru.netology.diploma.adapter.posts.OnInteractionListener
import ru.netology.diploma.adapter.posts.PostsAdapter
import ru.netology.diploma.databinding.FragmentMyPageBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.MyPageViewModel

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private val viewModel: MyPageViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyPageBinding.inflate(inflater, container, false)

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
                DividerItemDecoration.HORIZONTAL
            )
        )


        val jobsAdapter = JobAdapter(object : OnJobsInteractionListener {
            override fun onJob(job: Job) {
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
            binding.noPostsL.isVisible = state.empty && binding.groupLogined.isVisible
            binding.postLst.isVisible = !state.empty

            // binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Toast.makeText(requireContext(), "net error", Toast.LENGTH_LONG).show()
            }
        }


        viewModel.jobsDataState.observe(viewLifecycleOwner) { state ->
            binding.progressJobs.isVisible = state.loading
            binding.nojobsL.isVisible = state.empty && binding.groupLogined.isVisible
            binding.jobLst.isVisible = !state.empty
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
                binding.noPostsL.isVisible = postsAdapter.itemCount == 0 && binding.groupLogined.isVisible
            }
        }


        jobsAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh.endOfPaginationReached) {
                binding.nojobsL.isVisible = jobsAdapter.itemCount == 0 && binding.groupLogined.isVisible
            }
        }


        lifecycleScope.launchWhenCreated {
            postsAdapter.loadStateFlow.collectLatest { states ->
                binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
            }
        }


        /*   binding.swiperefresh.setOnRefreshListener {
               Log.e("ssss", "ssss swiperefresh")
           }*/


        binding.newJob.setOnClickListener {
            setFragmentResult("keyNewJob", Bundle())
        }

        binding.newPost.setOnClickListener {
            setFragmentResult("keyNewPost", Bundle())
        }


        return binding.root
    }
}
