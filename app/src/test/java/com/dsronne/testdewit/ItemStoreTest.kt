package com.dsronne.testdewit.domain

import com.dsronne.testdewit.Item
import com.dsronne.testdewit.ItemId
import com.dsronne.testdewit.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemStoreTest {
    private val repository = mockk<ItemRepository>()
    private val itemStore = ItemStore(repository)

    @Test
    fun `should save item when adding`() {
        // Given
        val listItem = ListItem(Item("test"))
        every { repository.save(any()) } returns Unit

        // When
        itemStore.add(listItem)

        // Then
        verify { repository.save(listItem) }
    }

    @Test
    fun `should return item when finding by id`() {
        // Given
        val listItem = ListItem(Item("test"))
        every { repository.findById(listItem.id) } returns listItem

        // When
        val result = itemStore.find(listItem.id)

        // Then
        assertEquals(listItem, result)
        verify { repository.findById(listItem.id) }
    }

    @Test
    fun `should initialize with items from constructor`() {
        // Given
        val items = listOf(
            ListItem(Item("item1")),
            ListItem(Item("item2"))
        )
        every { repository.save(any()) } returns Unit
        
        // When
        ItemStore(repository, items)

        // Then
        verify(exactly = items.size) { repository.save(any()) }
    }

    @Test
    fun `there is a root item in an item store with id and label root`() {
        every { repository.findById(ItemId("root"))} returns ListItem(Item("root", ItemId("root")))
        val itemStore = ItemStore(repository)
        assertEquals(ListItem(Item("root", ItemId("root"))), itemStore.root())

        verify(exactly = 1) { repository.findById(ItemId("root"))}
    }
}