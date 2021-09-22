package ru.netology.diploma.adapter.users

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.BuildConfig
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardUserBinding
import ru.netology.diploma.dto.User
import ru.netology.diploma.view.loadCircleCrop

interface OnUsersInteractionListener {
    fun onWall(post: User) {}
    fun onJobs(post: User) {}
    fun onEvents(post: User) {}
}

class UsersAdapter(
    private val onUsersInteractionListener: OnUsersInteractionListener,
) : PagingDataAdapter<User, RecyclerView.ViewHolder>(PostDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is User -> R.layout.card_user
            else -> error("unsupported type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_user -> {
                val binding =
                    CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return UserViewHolder(binding, onUsersInteractionListener)
            }


            else -> error("no such viewholder")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val item = getItem(position)
                if (item != null) {
                    holder.bind(item)
                } else {
                    Log.e("ssss", "xxxx null")
                }
            }

        }
    }
}


class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: OnUsersInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            userInfo.text = user.name
            userid.text = "#${user.id}"
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${user.avatar}")

            toEvents.setOnClickListener {
                onInteractionListener.onEvents(user)
            }

            toWall.setOnClickListener {
                onInteractionListener.onWall(user)
            }

            toJobs.setOnClickListener {
                onInteractionListener.onJobs(user)
            }
        }
    }
}

    class PostDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

