package com.dsronne.dewit.domain

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.domain.ports.ItemRepository
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.storage.InMemoryItemRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ItemStoreTest : io.kotest.core.spec.style.FunSpec({
    test("should save item when adding") {
        val repository = mockk<ItemRepository>()
        val itemStore = ItemStore(repository)
        val listItem = ListItem(Item("test"))
        every { repository.save(any()) } returns Unit

        itemStore.add(listItem)

        verify { repository.save(listItem) }
    }

    test("should return item when finding by id") {
        val repository = mockk<ItemRepository>()
        val itemStore = ItemStore(repository)
        val listItem = ListItem(Item("test"))
        every { repository.find(listItem.id) } returns listItem

        val result = itemStore.find(listItem.id)

        result shouldBe listItem
        verify { repository.find(listItem.id) }
    }

    test("should initialize with items from constructor") {
        val repository = mockk<ItemRepository>()
        val items = listOf(ListItem(Item("item1")), ListItem(Item("item2")))
        every { repository.save(any()) } returns Unit

        ItemStore(repository, items)

        verify(exactly = items.size) { repository.save(any()) }
    }

    test("there is a root item in an item store with id and label root") {
        val repository = mockk<ItemRepository>()
        every { repository.find(ItemId("root")) } returns ListItem(Item("root", ItemId("root")))

        val itemStore = ItemStore(repository)
        val root = itemStore.root()

        root shouldBe ListItem(Item("root", ItemId("root")))
        verify(exactly = 1) { repository.find(ItemId("root")) }
    }

    test("get children at path should return children of item at path") {
        val repository = mockk<ItemRepository>()
        val itemStore = ItemStore(repository)
        val parent = ListItem(Item("parent", ItemId("parent")))
        val child1 = ListItem(Item("child1", ItemId("child1")))
        val child2 = ListItem(Item("child2", ItemId("child2")))
        parent.add(child1)
        parent.add(child2)

        every { repository.find(ItemId("parent")) } returns parent
        every { repository.find(ItemId("child1")) } returns child1
        every { repository.find(ItemId("child2")) } returns child2

        val children = itemStore.getChildrenOf(ItemId("parent"))

        children shouldBe listOf(child1, child2)
        verify { repository.find(ItemId("parent")) }
        verify { repository.find(ItemId("child1")) }
        verify { repository.find(ItemId("child2")) }
    }

    test("program management test") {
        val repository = InMemoryItemRepository()
        val itemStore = ItemStore(repository)

        itemStore.initProgramManagement()

        val root = repository.find(ItemId("root"))!!
        val childLabels = root.children.map { repository.find(it)!!.label() }
        val expected = listOf("inbox", "todo", "projects", "waiting", "someday", "references")
        childLabels shouldBe expected
    }
})
