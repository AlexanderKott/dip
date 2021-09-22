package ru.netology.diploma.activity.apppages

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val adapterJ = JobAdapter(object : OnJobsInteractionListener {
            override fun onJobClick(job: Job) {
                Toast.makeText(requireContext(), "Click!", Toast.LENGTH_SHORT).
                        show()
            }

            override fun onJobRemove(job: Job) {
            }
        })


        binding.jlist.adapter = adapterJ.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapterJ::retry),
            footer = PagingLoadStateAdapter(adapterJ::retry)
        )


        arguments?.getLong("user")?.let {
                viewModel.loadJobsById(it)
        }


        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        binding.jlist.addItemDecoration(
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

            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.refreshJobs() }
                    .show()
            }
        }



        lifecycleScope.launchWhenCreated {
            viewModel.jobs.collectLatest {
                adapterJ.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
             adapterJ.loadStateFlow.collectLatest { states ->
                 binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
             }
         }


        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshJobs()
        }




        adapterJ.addLoadStateListener { loadState ->
            if (loadState.refresh.endOfPaginationReached) {
                if (adapterJ.itemCount == 0) {
                    binding.jlist.visibility = View.INVISIBLE
                    _binding?.emptyText?.isVisible =  true
                } else {
                    binding.jlist.visibility = View.VISIBLE
                    _binding?.emptyText?.isVisible = false
                }



            }
        }


    }
}
