package com.dsronne.testdewit.infrastructure

import com.dsronne.testdewit.Item
import com.dsronne.testdewit.ListItem
import org.junit.Assert.*
import org.junit.Test

class InMemoryItemRepositoryTest {
    private val repository = InMemoryItemRepository()

    @Test
    fun saveShouldPersistItem() {
        val item = ListItem(Item("test"))
        
        repository.save(item)
        
        val loaded = repository.findById(item.id)
        assertEquals(item, loaded)
    }

    @Test
    fun findByIdShouldReturnNullForNonExistentItem() {
        val loaded = repository.findById("non-existent-id")
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
