package com.dsronne.testdewit.storage

import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository

class InMemoryItemRepository : ItemRepository {
    private val items = mutableMapOf<ItemId, ListItem>()

    override fun save(item: ListItem) {
        items[item.id] = item
    }

    override fun find(id: ItemId): ListItem? {
        return items[id]
    }

    override fun findAll(): List<ListItem> {
        return items.values.toList()
    }
}