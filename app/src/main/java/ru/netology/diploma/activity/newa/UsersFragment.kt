package ru.netology.diploma.activity.newa

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.users.OnUsersInteractionListener
import ru.netology.diploma.adapter.users.UsersAdapter
import ru.netology.diploma.databinding.FragmentUsersBinding
import ru.netology.diploma.dto.User
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)

               val adapter = UsersAdapter(object : OnUsersInteractionListener {
                   override fun onEdit(user: User) {
                   }

                   override fun onLike(user: User) {
                   }

                   override fun onRemove(user: User) {
                   }


                   override fun onShare(user: User) {

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
                   binding.progress.isVisible = state.loading
                   binding.swiperefresh.isRefreshing = state.refreshing
                   if (state.error) {
                       Log.e("ssss", "dataState error")
                    /*  Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                           .setAction(R.string.retry_loading) { viewModel.loadUsers() }
                           .show()*/
                       Toast.makeText(requireContext(), "net error", Toast.LENGTH_LONG).
                               show()
                   }
               }

               lifecycleScope.launchWhenCreated {
                   viewModel.cachedusers.collectLatest {
                       Log.e("ssss", "submitData dataUsers")
                       adapter.submitData(it)
                   }
               }

             /*  lifecycleScope.launchWhenCreated {
                   adapter.loadStateFlow.collectLatest { states ->
                       binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading
                   }
               }*/


               binding.swiperefresh.setOnRefreshListener {
                   viewModel.loadUsers()
                   Log.e("ssss", "ssss swiperefresh")

               }



        return binding.root
    }
}
