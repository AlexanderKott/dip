package ru.kot1.demo.activity.pages

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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.kot1.demo.R
import ru.kot1.demo.activity.utils.PagingLoadStateAdapter
import ru.kot1.demo.adapter.events.EventsAdapter
import ru.kot1.demo.adapter.events.OnEventsInteractionListener
import ru.kot1.demo.databinding.FragmentEventsBinding
import ru.kot1.demo.dto.Event
import ru.kot1.demo.viewmodel.EventAllViewModel

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
            _binding?.swiperefresh?.isRefreshing = state.refreshing
            _binding?.progress?.isVisible = state.loading && _binding?.swiperefresh?.isVisible == false

            if (!state.refreshing && !state.loading && !state.empty) {
                _binding?.elist?.visibility = View.VISIBLE
            } else {
                _binding?.elist?.visibility = View.INVISIBLE
            }
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

                  if (states.refresh.endOfPaginationReached) {
                      _binding?.emptyText?.isVisible = adapter.itemCount == 0
                  }
              }
          }

        _binding?.swiperefresh?.setOnRefreshListener {
            viewModel.loadEvents()
        }

    }
}
