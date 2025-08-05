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
}