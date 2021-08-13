package ru.netology.diploma.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.netology.diploma.R
import ru.netology.diploma.activity.newa.EventsFragment
import ru.netology.diploma.activity.newa.PostsFragment
import ru.netology.diploma.activity.newa.UsersFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
)


class SectionsPagerAdapter(private val context: Context , fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UsersFragment()
            1 -> PostsFragment()
            2 -> EventsFragment()
            else -> throw  UnsupportedClassVersionError()
        }
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }


    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}