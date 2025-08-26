package com.dsronne.dewit.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem

class ItemPagerAdapter(
    fa: FragmentActivity,
    items: List<ListItem>
) : FragmentStateAdapter(fa) {

    private val items: MutableList<ListItem> = items.toMutableList()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment =
        ItemFragment.newInstance(items[position])

    // Stable IDs so ViewPager2 can manage fragment lifecycles during updates
    override fun getItemId(position: Int): Long = stableIdFor(items[position].id)

    override fun containsItem(itemId: Long): Boolean = items.any { stableIdFor(it.id) == itemId }

    fun submitItems(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun indexOf(itemId: ItemId): Int = items.indexOfFirst { it.id == itemId }

    fun itemsSnapshot(): List<ListItem> = items.toList()

    private fun stableIdFor(id: ItemId): Long = id.id.hashCode().toLong()
}
