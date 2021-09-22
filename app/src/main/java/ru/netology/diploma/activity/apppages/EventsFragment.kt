package ru.netology.diploma.activity.apppages

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.events.EventsAdapter
import ru.netology.diploma.adapter.events.OnEventsInteractionListener
import ru.netology.diploma.databinding.FragmentEventsBinding
import ru.netology.diploma.databinding.FragmentJobsBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.viewmodel.EventViewModel

class EventsFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!


    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val adapter = EventsAdapter(object : OnEventsInteractionListener {
            override fun onEdit(user: Event) {
            }

            override fun onLike(user: Event) {
            }

            override fun onRemove(user: Event) {
            }


            override fun onShare(user: Event) {

            }
        })


        binding.elist.adapter = adapter
            .withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(adapter::retry),
                footer = PagingLoadStateAdapter(adapter::retry)
            )


        binding.elist.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        binding.elist.addItemDecoration(
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


        arguments?.getLong("user")?.let {
            viewModel.getEventById(it)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading && !state.refreshing
            binding.swiperefresh.isRefreshing = state.refreshing

          /*  if (!state.refreshing && !state.loading && !state.empty) {
                binding.list.visibility = View.VISIBLE
            } else {
                binding.list.visibility = View.INVISIBLE
            }*/

            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.refreshEvents() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.feedEvents.collectLatest {
                adapter.submitData(it)
            }
        }

          lifecycleScope.launchWhenCreated {
              adapter.loadStateFlow.collectLatest { states ->
                  binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
              }
          }


        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshEvents()
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh.endOfPaginationReached) {
                _binding?.emptyText?.isVisible = adapter.itemCount == 0
            }
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
