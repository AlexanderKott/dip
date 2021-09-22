package ru.netology.diploma.activity

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.diploma.R
import ru.netology.diploma.activity.apppages.*

  val  TAB_TITLES = arrayOf(
    R.string.tab_text_0,
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
)

val  TAB_ICONS = arrayOf(
    R.drawable.ic_baseline_user_placeholder,
    R.drawable.users,
    R.drawable.posts,
    R.drawable.events,
)




class SectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
          return when (position) {
           0 -> MyPageFragment()
           1 -> UsersFragment()
           2 -> PostsAllFragment()
           3 -> EventsAllFragment()
           else -> throw  UnsupportedClassVersionError()
       }
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }
}