package ru.netology.diploma.adapter.events

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
import ru.netology.diploma.dto.Event
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.Post2
import ru.netology.diploma.model.AdModel
import ru.netology.diploma.model.FeedModel
import ru.netology.diploma.model.PostModel
import ru.netology.diploma.view.load
import ru.netology.diploma.view.loadCircleCrop

interface OnEventsInteractionListener {
    fun onLike(post: Event) {}
    fun onEdit(post: Event) {}
    fun onRemove(post: Event) {}
    fun onShare(post: Event) {}
    fun onAdClick (ad: AdModel) {}
}

class EventsAdapter(
    private val onInteractionListener: OnEventsInteractionListener,
) : PagingDataAdapter<Event, RecyclerView.ViewHolder>(PostDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
       return R.layout.card_event
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_event -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return EventsViewHolder(binding, onInteractionListener)
            }
            else -> error("no such viewholder")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is EventsViewHolder -> {
                val item = getItem(position) as Event
                holder.bind(item)
            }

        }
    }
}

data class StateX(val id :Long, val like :Boolean)

class EventsViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnEventsInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {
            author.text = event.author
            published.text = event.published.toString()
            content.text = event.content
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${event.authorAvatar}")
            val x = root.tag

           if (x is  ObjectAnimator) {
              x.cancel()
           }

            like.setOnClickListener {
                root.tag = StateX(event.id.toLong(), !like.isChecked)

                val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.25F, 1F)
               val x = ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY)
                root.tag = x
                   x.apply {
                    duration = 5_000
                    repeatCount = 10000000
                    interpolator = BounceInterpolator()
                }  .start()
                onInteractionListener.onLike(event)
            }


            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }
        }
    }
}





class PostDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        if (oldItem.javaClass != newItem.javaClass) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }

   /* override fun getChangePayload(oldItem: FeedModel, newItem: FeedModel): Any? {
        if (newItem is PostModel) {
            Payload( newItem.post)
        }


    }*/
}

data class Payload (val liked: Boolean? = null)
