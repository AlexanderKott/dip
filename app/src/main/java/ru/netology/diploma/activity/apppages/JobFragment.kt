package ru.netology.diploma.activity.apppages

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.jobs.JobAdapter
import ru.netology.diploma.adapter.jobs.OnJobsInteractionListener
import ru.netology.diploma.databinding.FragmentJobsBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.viewmodel.JobsViewModel

@AndroidEntryPoint
class JobFragment : Fragment() {
    private val viewModel: JobsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentJobsBinding.inflate(inflater, container, false)


        val adapter = JobAdapter(object : OnJobsInteractionListener {
            override fun onJob(job: Job) {
            }
        })


        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )


        arguments?.getLong("user")?.let {
                viewModel.loadJobsById(it)
        }

        binding.list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        binding.list.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    itemPosition: Int,
                    parent: RecyclerView
                ) {
                    outRect.left += offesetH
                    outRect.right += offesetH
                }
            }
        )


        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing

            binding.emptyText.isVisible = state.empty

            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.refreshPosts() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.jobs.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
             adapter.loadStateFlow.collectLatest { states ->
                 binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
             }
         }


        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
        }

        return binding.root
    }
}
