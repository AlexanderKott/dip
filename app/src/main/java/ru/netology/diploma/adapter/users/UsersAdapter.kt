package ru.netology.diploma.adapter.users

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardUserBinding
import ru.netology.diploma.dto.User

interface OnUsersInteractionListener {
    fun onLike(post: User) {}
    fun onEdit(post: User) {}
    fun onRemove(post: User) {}
    fun onShare(post: User) {}
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
        when (holder){
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
            userInfo.setText("${user.id}  ${user.name}")
            Log.e("ssss", "xxxx ${user.id}  ${user.name}")
            /*avatar.load("${BuildConfig.BASE_URL}/media/${ad.picture}")

            avatar.setOnClickListener {
                onInteractionListener.onAdClick(ad)
            }*/
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

   /* override fun getChangePayload(oldItem: FeedModel, newItem: FeedModel): Any? {
        if (newItem is PostModel) {
            Payload( newItem.post)
        }


    }*/
}

