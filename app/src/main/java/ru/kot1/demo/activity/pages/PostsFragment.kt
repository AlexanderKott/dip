package ru.kot1.demo.activity.pages

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kot1.demo.R
import ru.kot1.demo.adapter.posts.OnInteractionListener
import ru.kot1.demo.activity.utils.PagingLoadStateAdapter
import ru.kot1.demo.adapter.posts.PostsAdapter
import ru.kot1.demo.databinding.FragmentPostsBinding
import ru.kot1.demo.dto.Post
import ru.kot1.demo.viewmodel.PostAllViewModel
import ru.kot1.demo.viewmodel.PostViewModel

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {
    private val viewModel: PostViewModel by activityViewModels()
    private val viewModelAll: PostAllViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val binding = FragmentPostsBinding.bind(view)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onNotLogined(post: Post) {
            }

            override fun onVideoClick(post: Post) {
            }

            override fun onAudioClick(post: Post) {
            }

            override fun onPlaceClick(post: Post) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:<" + post.coords?.lat.toString() +
                            ">,<" + post.coords?.long.toString() +
                            ">?q=<" + post.coords?.lat.toString() +
                            ">,<" + post.coords?.long.toString() +
                            ">(" + "The place" +
                            ")")
                )
                startActivity(intent)
            }
            override fun onEdit(post: Post, position: Int) {
                //viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModelAll.setLikeOrDislike(post)
            }

            override fun onRemove(post: Post) {
            }

            override fun onShare(post: Post) {
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


        binding.plist.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )


        arguments?.getLong("user")?.let {
                viewModel.getWallById(it)
        }

        binding.plist.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        val offesetH = resources.getDimensionPixelSize(R.dimen.common_spacing)
        binding.plist.addItemDecoration(
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
            binding.swiperefresh.isRefreshing = state.refreshing
            binding.progress.isVisible = state.loading && !binding.swiperefresh.isVisible

            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.refreshPosts() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { states ->
                binding.swiperefresh.isRefreshing = states.refresh is LoadState.Loading

                if (states.refresh is LoadState.NotLoading){
                binding.emptyText?.isVisible = adapter.itemCount == 0
            }

            }
        }



        lifecycleScope.launchWhenCreated {
            viewModel.feedModels.collectLatest {
                adapter.submitData(it)
            }
        }




        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }




    }
}