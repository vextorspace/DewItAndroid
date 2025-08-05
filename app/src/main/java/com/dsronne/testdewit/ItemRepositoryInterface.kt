package com.dsronne.testdewit.domain.ports

import com.dsronne.testdewit.ListItem

interface ItemRepository {
    fun save(item: ListItem)
    fun findById(id: String): ListItem?
    fun findAll(): List<ListItem>
}
