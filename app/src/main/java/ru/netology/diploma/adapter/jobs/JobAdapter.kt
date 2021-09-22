package ru.netology.diploma.adapter.jobs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardJobBinding
import ru.netology.diploma.databinding.CardPostBinding
import ru.netology.diploma.dto.Job

interface OnJobsInteractionListener {
    fun onJobClick(job: Job)
    fun onJobRemove(job: Job)
}

class JobAdapter(
    private val onUsersInteractionListener: OnJobsInteractionListener,
) : PagingDataAdapter<Job, RecyclerView.ViewHolder>(JobDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Job -> R.layout.card_job
            else -> error("unsupported type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_job -> {
                val binding =
                    CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    private val binding: CardJobBinding,
    private val onInteractionListener: OnJobsInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {

            cname.setText("#${job.id} ${job.name}")
            position.setText(job.position)

            binding.root.setOnClickListener {
                onInteractionListener.onJobClick(job)
            }

            menu.isVisible = job.belongsToMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.job_menu, true)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onJobClick(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()

            }
        }
    }
}

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }

