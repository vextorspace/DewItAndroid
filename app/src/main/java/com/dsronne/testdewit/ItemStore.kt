package com.dsronne.testdewit.domain

import com.dsronne.testdewit.Item
import com.dsronne.testdewit.ItemBrowser
import com.dsronne.testdewit.ItemId
import com.dsronne.testdewit.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository

class ItemStore(
    private val repository: ItemRepository,
    items: List<ListItem> = emptyList()
) : ItemBrowser {
    init {
        items.forEach { repository.save(it) }
    }

    fun add(item: ListItem) {
        repository.save(item)
    }

    override fun find(id: ItemId): ListItem? {
        return repository.find(id)
    }

    override fun root(): ListItem {
        return find(ItemId("root")) ?: ListItem(Item("root", ItemId("root"))).also { add(it) }
    }

    override fun getChildrenOf(itemId: ItemId): List<ListItem> {
        val parent = find(itemId) ?: return emptyList()
        return parent.children.mapNotNull { childId -> find(childId) }
    }
}