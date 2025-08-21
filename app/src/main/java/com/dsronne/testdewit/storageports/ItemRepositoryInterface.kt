package com.dsronne.dewit.domain.ports

import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem

interface ItemRepository {
    fun save(item: ListItem)
    fun find(id: ItemId): ListItem?
    fun findAll(): List<ListItem>
    fun update(item: ListItem)
}
