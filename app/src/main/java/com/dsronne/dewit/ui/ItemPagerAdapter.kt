package com.dsronne.dewit.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.ItemFragment

class ItemPagerAdapter(
    fa: FragmentActivity,
    private val items: List<ListItem>,
    private val itemStore: ItemStore
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment =
        ItemFragment.newInstance(items[position], itemStore)
}
