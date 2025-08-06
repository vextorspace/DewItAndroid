package com.dsronne.testdewit.domain.ports

import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem

interface ItemRepository {
    fun save(item: ListItem)
    fun find(id: ItemId): ListItem?
    fun findAll(): List<ListItem>
    fun update(item: ListItem)
}
