package ru.netology.diploma.activity.apppages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.viewmodel.JobsViewModel

class NewJobFragment : Fragment() {
    private val viewModel: JobsViewModel by activityViewModels()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)

        arguments?.getLong("user")?.let {
        }

        with(binding) {
         done.setOnClickListener {
                val cname = compName.text.toString()
                val pos = position.text.toString()
             viewModel.postNewJob(cname,pos, 0L, 0L)
             activity?.onBackPressed()
        }

    }


        return binding.root
    }
}
