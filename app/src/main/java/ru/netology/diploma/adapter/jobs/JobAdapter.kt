package ru.netology.diploma.adapter.jobs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardPostBinding
import ru.netology.diploma.databinding.CardUserBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.User

interface OnJobsInteractionListener {
    fun onJob(job: Job) {}
}

class JobAdapter(
    private val onUsersInteractionListener: OnJobsInteractionListener,
) : PagingDataAdapter<Job, RecyclerView.ViewHolder>(PostDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Job -> R.layout.card_post
            else -> error("unsupported type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return JobViewHolder(binding, onUsersInteractionListener)
            }
            else -> error("no such viewholder")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is JobViewHolder -> {
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


class JobViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnJobsInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {

            author.setText("${job.id}  ${job.name} ${job.position}")

            author.setOnClickListener {
                onInteractionListener.onJob(job)
            }

        }
    }
}

    class PostDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }

