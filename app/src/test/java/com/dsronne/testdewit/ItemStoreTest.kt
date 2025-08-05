package com.dsronne.testdewit

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemStoreTest {

    @Test
    fun itemStoreFindsListItemById() {
        val item = Item("test")
        val listItem = ListItem(item)
        val itemStore = ItemStore(listOf(listItem))

        assertEquals(listItem, itemStore.find(item.id))
    }

    @Test
    fun itemStoreReturnsNullForUnknownId() {
        val itemStore = ItemStore(emptyList())
        assertEquals(null, itemStore.find("unknown-id"))
    }
    
    @Test
    fun itemStoreAdd() {
        val item = Item("test")
        val listItem = ListItem(item)
        val itemStore = ItemStore(listOf(listItem))

        val newItem = Item("new test")
        val newListItem = ListItem(newItem)
        itemStore.add(newListItem)

        assertEquals(newListItem, itemStore.find(newItem.id))
    }

}