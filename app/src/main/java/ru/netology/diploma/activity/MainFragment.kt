package ru.netology.diploma.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.databinding.FragmentFeedBinding
import ru.netology.diploma.view.SectionsPagerAdapter
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)


        binding.tabz.setupWithViewPager(binding.vPager)
        val sectionsPagerAdapter = SectionsPagerAdapter(
            requireContext(),
            getChildFragmentManager()
        )

        binding.vPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabz
        tabLayout.setupWithViewPager(binding.vPager)


        return binding.root
    }
}
