package com.dsronne.testdewit.domain

import com.dsronne.testdewit.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository

class ItemStore(
    private val repository: ItemRepository,
    items: List<ListItem> = emptyList()
) {
    init {
        items.forEach { repository.save(it) }
    }

    fun find(id: String): ListItem? {
        return repository.findById(id)
    }

    fun add(item: ListItem) {
        repository.save(item)
    }
}