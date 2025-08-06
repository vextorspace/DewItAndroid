package com.dsronne.testdewit.domain

import com.dsronne.testdewit.Item
import com.dsronne.testdewit.ItemId
import com.dsronne.testdewit.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository

class ItemStore(
    private val repository: ItemRepository,
    items: List<ListItem> = emptyList()
) {
    init {
        items.forEach { repository.save(it) }
    }

    fun find(id: ItemId): ListItem? {
        return repository.findById(id)
    }

    fun add(item: ListItem) {
        repository.save(item)
    }

    fun root(): ListItem {
        return find(ItemId("root")) ?: ListItem(Item("root", ItemId("root"))).also { add(it) }
    }
}