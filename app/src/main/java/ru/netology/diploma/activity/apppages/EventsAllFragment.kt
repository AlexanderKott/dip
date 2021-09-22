package ru.netology.diploma.activity.apppages

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.netology.diploma.databinding.FragmentPostsBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.viewmodel.EventAllViewModel

class EventsAllFragment : Fragment() {
    private val viewModel: EventAllViewModel by activityViewModels()
    private var _binding: FragmentEventsBinding? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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


        _binding?.elist?.adapter = adapter
            .withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(adapter::retry),
                footer = PagingLoadStateAdapter(adapter::retry)
            )



        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        _binding?.elist?.addItemDecoration(
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
            _binding?.progress?.isVisible = state.loading && !state.refreshing
            _binding?.swiperefresh?.isRefreshing = state.refreshing

            if (!state.refreshing && !state.loading && !state.empty) {
                _binding?.elist?.visibility = View.VISIBLE
            } else {
                _binding?.elist?.visibility = View.INVISIBLE
            }

            //_binding?.emptyText?.isVisible = state.empty

            if (state.error) {
                Snackbar.make(_binding!!.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadEvents() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.cachedevents.collectLatest {
                adapter.submitData(it)

            }
        }




          lifecycleScope.launchWhenCreated {
              adapter.loadStateFlow.collectLatest { states ->
                  _binding?.swiperefresh?.isRefreshing = states.refresh is LoadState.Loading
                  _binding?.errorOccured?.isVisible = states.refresh is LoadState.Error
              }
          }


        _binding?.swiperefresh?.setOnRefreshListener {
            viewModel.loadEvents()
        }

        adapter.addLoadStateListener { loadState ->

            if (loadState.refresh.endOfPaginationReached) {
                _binding?.emptyText?.isVisible = adapter.itemCount == 0
            }
        }
    }
}
