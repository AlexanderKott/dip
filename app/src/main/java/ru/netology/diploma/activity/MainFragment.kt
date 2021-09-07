package ru.netology.diploma.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?//
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

       // binding.appBarLayout   .setTitle("123")


        binding.tabz.setupWithViewPager(binding.vPager)
        val sectionsPagerAdapter = SectionsPagerAdapter(
            requireContext(),
            getChildFragmentManager()
        )

        binding.vPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabz
        tabLayout.setupWithViewPager(binding.vPager)


        childFragmentManager.setFragmentResultListener("keyEvents",
            viewLifecycleOwner) { requestKey, bundle ->
            setFragmentResult(requestKey, bundle)
        }

        childFragmentManager.setFragmentResultListener("keyWall",
            viewLifecycleOwner) { requestKey, bundle ->
            setFragmentResult(requestKey, bundle)
        }

        childFragmentManager.setFragmentResultListener("keyJobs",
            viewLifecycleOwner) { requestKey, bundle ->
            setFragmentResult(requestKey, bundle)
        }

        childFragmentManager.setFragmentResultListener("keyNewJob",
            viewLifecycleOwner) { requestKey, bundle ->
            setFragmentResult(requestKey, bundle)
        }

        childFragmentManager.setFragmentResultListener("keyNewPost",
            viewLifecycleOwner) { requestKey, bundle ->
            setFragmentResult(requestKey, bundle)
        }





        return binding.root
    }
}
