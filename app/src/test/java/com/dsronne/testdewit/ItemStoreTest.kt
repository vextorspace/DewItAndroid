package com.dsronne.testdewit.domain

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository
import com.dsronne.testdewit.storage.ItemStore
import com.dsronne.testdewit.storage.InMemoryItemRepository
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
        every { repository.find(listItem.id) } returns listItem

        // When
        val result = itemStore.find(listItem.id)

        // Then
        assertEquals(listItem, result)
        verify { repository.find(listItem.id) }
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
        every { repository.find(ItemId("root"))} returns ListItem(Item("root", ItemId("root")))
        val itemStore = ItemStore(repository)
        assertEquals(ListItem(Item("root", ItemId("root"))), itemStore.root())

        verify(exactly = 1) { repository.find(ItemId("root"))}
    }

    @Test
    fun `getChildrenAtPath should return children of item at path`() {
        // Given
        val parent = ListItem(Item("parent", ItemId("parent")))
        val child1 = ListItem(Item("child1", ItemId("child1")))
        val child2 = ListItem(Item("child2", ItemId("child2")))
        parent.add(child1)
        parent.add(child2)
        
        every { repository.find(ItemId("parent")) } returns parent
        every { repository.find(ItemId("child1")) } returns child1
        every { repository.find(ItemId("child2")) } returns child2

        // When
        val children = itemStore.getChildrenOf(ItemId("parent"))

        // Then
        assertEquals(listOf(child1, child2), children)
        verify { repository.find(ItemId("parent")) }
        verify { repository.find(ItemId("child1")) }
        verify { repository.find(ItemId("child2")) }
    }
    @Test
    fun `program management test`() {
        val repository = InMemoryItemRepository()
        val itemStore = ItemStore(repository)

        itemStore.initProgramManagement()

        val root = repository.find(ItemId("root"))!!
        val childLabels = root.children.map { repository.find(it)!!.label() }
        val expected = listOf("inbox", "todo", "projects", "waiting", "someday", "references")
        assertEquals(expected, childLabels)
    }
}
