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
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class PostsFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostsBinding.inflate(inflater, container, false)


        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post2) {
                //viewModel.edit(post)
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


        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )


        arguments?.getLong("user")?.let {
                viewModel.getWallById(it)
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
            viewModel.feedModels.collectLatest {
                adapter.submitData(it)
                Log.e("OkHttpClient", "submitData posts")
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
