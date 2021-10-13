package ru.kot1.demo.activity.pages

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import ru.kot1.demo.R
import ru.kot1.demo.activity.utils.PagingLoadStateAdapter
import ru.kot1.demo.adapter.events.EventsAdapter
import ru.kot1.demo.adapter.events.OnEventsInteractionListener
import ru.kot1.demo.databinding.FragmentEventsBinding
import ru.kot1.demo.dto.Event
import ru.kot1.demo.viewmodel.EventViewModel


class EventsFragment : Fragment(R.layout.fragment_events) {
    private val viewModel: EventViewModel by activityViewModels()




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         var binding = FragmentEventsBinding.bind(view)

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
            binding.swiperefresh.isRefreshing = state.refreshing
            binding.progress.isVisible = state.loading && !binding.swiperefresh.isVisible

                if (state.error) {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) { viewModel.refreshEvents() }
                        .show()
                }
        }



      lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { states ->
                binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
                //.snapshot().items
                if (states.refresh is LoadState.NotLoading) {
                    binding.emptyText.isVisible = adapter.itemCount == 0
                }

            }
        }


      lifecycleScope.launchWhenCreated {
            viewModel.feedEvents.collectLatest {
                adapter.submitData(it)
            }
        }



       binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshEvents()
        }

    }



}
