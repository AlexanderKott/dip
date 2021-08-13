package ru.netology.diploma.adapter.posts

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.BuildConfig
import ru.netology.diploma.R
import ru.netology.diploma.databinding.AdPostBinding
import ru.netology.diploma.databinding.CardPostBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.model.FeedModel
import ru.netology.diploma.model.PostModel
import ru.netology.diploma.view.load
import ru.netology.diploma.view.loadCircleCrop

interface OnInteractionListener {
    fun onLike(post: Post2) {}
    fun onEdit(post: Post2) {}
    fun onRemove(post: Post2) {}
    fun onShare(post: Post2) {}
    fun onAdClick (ad: AdModel) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedModel, RecyclerView.ViewHolder>(PostDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
       return when (getItem(position)) {
            is AdModel -> R.layout.ad_post
            is PostModel -> R.layout.card_post
            else -> error("unsupported type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.ad_post -> {
                val binding =
                    AdPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AdViewHolder(binding, onInteractionListener)
            }

            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(binding, onInteractionListener)
            }
            else -> error("no such viewholder")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is PostViewHolder -> {
                val item = getItem(position) as PostModel
                holder.bind(item.post)
            }
            is AdViewHolder -> {
                val item = getItem(position) as AdModel
                holder.bind(item)
            }
        }
    }
}

data class StateX(val id :Long, val like :Boolean)

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post2) {
        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            val x = root.tag

           if (x is  ObjectAnimator) {
              x.cancel()

               if (post.likedByMe) {
                   x.start()
               }
           }

           //// like.text = "${post.likes}"

            like.setOnClickListener {
                root.tag = StateX(post.id, !like.isChecked)

                val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.25F, 1F)
               val x = ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY)
                root.tag = x
                   x.apply {
                    duration = 5_000
                    repeatCount = 10000000
                    interpolator = BounceInterpolator()
                }  .start()
                onInteractionListener.onLike(post)
            }


         //   menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

         /*   menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    // TODO: if we don't have other options, just remove dots
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }*/

           /* like.setOnClickListener {
                onInteractionListener.onLike(post)
            }*/

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}


class AdViewHolder(
    private val binding: AdPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: AdModel) {
        binding.apply {
            avatar.load("${BuildConfig.BASE_URL}/media/${ad.picture}")

            avatar.setOnClickListener {
                onInteractionListener.onAdClick(ad)
            }
        }
    }
}





class PostDiffCallback : DiffUtil.ItemCallback<FeedModel>() {
    override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
        if (oldItem.javaClass != newItem.javaClass) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
        return oldItem == newItem
    }

   /* override fun getChangePayload(oldItem: FeedModel, newItem: FeedModel): Any? {
        if (newItem is PostModel) {
            Payload( newItem.post)
        }


    }*/
}

data class Payload (val liked: Boolean? = null)
