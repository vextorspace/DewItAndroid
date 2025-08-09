package com.dsronne.testdewit.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dsronne.testdewit.datamodel.ListItem

class ItemPagerAdapter(
    fa: FragmentActivity,
    private val items: List<ListItem>
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment =
        ItemFragment.newInstance(items[position])
}
