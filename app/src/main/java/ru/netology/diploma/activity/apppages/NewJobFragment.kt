package ru.netology.diploma.activity.apppages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.databinding.FragmentPostsBinding
import ru.netology.diploma.viewmodel.JobsViewModel

class NewJobFragment : Fragment() {
    private val viewModel: JobsViewModel by activityViewModels()
    private var _binding: FragmentNewJobBinding? = null
    private val binding get() = _binding!!

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewJobBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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



    }
}
