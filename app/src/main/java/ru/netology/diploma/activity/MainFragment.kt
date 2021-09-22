package ru.netology.diploma.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentMainBinding
import ru.netology.diploma.repository.AppEntities
import ru.netology.diploma.repository.PostRepository
import ru.netology.diploma.view.ZoomOutPageTransformer
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repo: AppEntities

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?//
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pager.adapter = SectionsPagerAdapter(this)
       binding.pager.setOffscreenPageLimit(1)

        val clickCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                repo.savePageToPrefs(position)
            }
        }

        binding.pager.registerOnPageChangeCallback(clickCallback)

        TabLayoutMediator(binding.tabz, binding.pager) { tab, position ->
          tab.setText(TAB_TITLES[position])
          tab.setIcon(TAB_ICONS[position])
        }.attach()

        binding.pager.setCurrentItem(repo.getSavedPage(), false)
        binding.pager.setPageTransformer(ZoomOutPageTransformer())

        childFragmentManager.
            setFragmentResultListener(
                "keyMainFragment",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }
        childFragmentManager.
            setFragmentResultListener(
                "keyEvents",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }
        childFragmentManager.
            setFragmentResultListener(
                "keyWall",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }

            childFragmentManager.setFragmentResultListener(
                "keyJobs",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }
        childFragmentManager.
            setFragmentResultListener(
                "keyNewJob",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }

        childFragmentManager.
            setFragmentResultListener(
                "keyNewPost",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                setFragmentResult(requestKey, bundle)
            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
