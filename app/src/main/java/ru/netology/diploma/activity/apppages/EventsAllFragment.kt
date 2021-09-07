package ru.netology.diploma.activity.apppages

import android.graphics.Rect
import android.os.Bundle
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.events.EventsAdapter
import ru.netology.diploma.adapter.events.OnEventsInteractionListener
import ru.netology.diploma.databinding.FragmentEventsBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.viewmodel.EventAllViewModel

class EventsAllFragment : Fragment() {
    private val viewModel: EventAllViewModel by activityViewModels()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventsBinding.inflate(inflater, container, false)

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


        binding.list.adapter = adapter
            .withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(adapter::retry),
                footer = PagingLoadStateAdapter(adapter::retry)
            )


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
            binding.progress.isVisible = state.loading && !state.refreshing
            binding.swiperefresh.isRefreshing = state.refreshing

            if (!state.refreshing && !state.loading && !state.empty) {
                binding.list.visibility = View.VISIBLE
            } else {
                binding.list.visibility = View.INVISIBLE
            }

            binding.emptyText.isVisible = state.empty

            if (state.error) {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.cachedevents.collectLatest {
                adapter.submitData(it)

            }
        }




          lifecycleScope.launchWhenCreated {
              adapter.loadStateFlow.collectLatest { states ->
                  binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
              }
          }


        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadEvents()
        }



        return binding.root
    }
}
