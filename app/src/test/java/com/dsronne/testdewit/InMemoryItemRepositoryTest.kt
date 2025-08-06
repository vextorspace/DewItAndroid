package com.dsronne.testdewit.infrastructure

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.storage.InMemoryItemRepository
import org.junit.Assert.*
import org.junit.Test

class InMemoryItemRepositoryTest {
    private val repository = InMemoryItemRepository()

    @Test
    fun saveShouldPersistItem() {
        val item = ListItem(Item("test"))
        
        repository.save(item)
        
        val loaded = repository.find(item.id)
        assertEquals(item, loaded)
    }

    @Test
    fun findShouldReturnNullForNonExistentItem() {
        val loaded = repository.find(ItemId("non-existent-id"))
        assertNull(loaded)
    }

    @Test
    fun findAllShouldReturnAllSavedItems() {
        val item1 = ListItem(Item("test1"))
        val item2 = ListItem(Item("test2"))
        
        repository.save(item1)
        repository.save(item2)
        
        val allItems = repository.findAll()
        assertEquals(2, allItems.size)
        assertTrue(allItems.contains(item1))
        assertTrue(allItems.contains(item2))
    }
}
