
package com.dsronne.testdewit.infrastructure

import com.dsronne.testdewit.ItemId
import com.dsronne.testdewit.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository

class InMemoryItemRepository : ItemRepository {
    private val items = mutableMapOf<ItemId, ListItem>()

    override fun save(item: ListItem) {
        items[item.id] = item
    }

    override fun findById(id: ItemId): ListItem? {
        return items[id]
    }

    override fun findAll(): List<ListItem> {
        return items.values.toList()
    }
}
