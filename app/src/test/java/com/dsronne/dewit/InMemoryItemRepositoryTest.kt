package com.dsronne.dewit.infrastructure

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.InMemoryItemRepository
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class InMemoryItemRepositoryTest : io.kotest.core.spec.style.FunSpec({
    test("save should persist item") {
        val repository = InMemoryItemRepository()
        val item = ListItem(Item("test"))

        repository.save(item)

        repository.find(item.id) shouldBe item
    }

    test("find should return null for non-existent item") {
        val repository = InMemoryItemRepository()
        repository.find(ItemId("non-existent-id")).shouldBeNull()
    }

    test("findAll should return all saved items") {
        val repository = InMemoryItemRepository()
        val item1 = ListItem(Item("test1"))
        val item2 = ListItem(Item("test2"))

        repository.save(item1)
        repository.save(item2)

        val allItems = repository.findAll()
        allItems.size shouldBe 2
        allItems.shouldContain(item1)
        allItems.shouldContain(item2)
    }
})
