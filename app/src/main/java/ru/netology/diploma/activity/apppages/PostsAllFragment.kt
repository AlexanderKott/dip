package ru.netology.diploma.activity.apppages

import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.posts.OnInteractionListener
import ru.netology.diploma.adapter.PagingLoadStateAdapter
import ru.netology.diploma.adapter.posts.PostsAdapter
import ru.netology.diploma.databinding.FragmentPostsBinding
import ru.netology.diploma.databinding.FragmentUsersBinding
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.viewmodel.PostAllViewModel

@AndroidEntryPoint
class PostsAllFragment : Fragment() {
    private val viewModel: PostAllViewModel by activityViewModels()
    private var _binding: FragmentPostsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post2) {
            }

            override fun onLike(post: Post2) {
            }

            override fun onRemove(post: Post2) {
            }

            override fun onAdClick(ad: AdModel) {
                Toast.makeText(requireContext(), "Add clicked ${ad.id}", Toast.LENGTH_LONG).show()
            }

            override fun onShare(post: Post2) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })


        _binding?.plist?.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )



      /*  _binding?.list?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )*/

        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        _binding?.plist?.addItemDecoration(
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
            _binding?.progress?.isVisible = state.loading
            _binding?.swiperefresh?.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(_binding!!.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.feedModels.collectLatest {
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
            viewModel.refreshPosts()
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
