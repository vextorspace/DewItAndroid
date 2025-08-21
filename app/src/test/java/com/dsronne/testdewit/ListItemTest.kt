package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class ListItemTest : io.kotest.core.spec.style.FunSpec({
    test("default constructor provides default data") {
        val listItem = ListItem()
        listItem.data.shouldNotBeNull()
    }

    test("addChild adds child id") {
        val parent = ListItem()
        val child = ListItem()
        parent.add(child)
        parent.children.shouldContain(child.id)
    }

    test("label returns item label") {
        val label = "test label"
        val item = Item(label)
        val listItem = ListItem(item)
        listItem.label() shouldBe label
    }

    test("addChildren ids order is preserved") {
        val parent = ListItem()
        val firstChild = ListItem()
        val secondChild = ListItem()
        parent.add(firstChild)
        parent.add(secondChild)
        parent.children.last() shouldBe secondChild.id
    }

    test("id returns item id") {
        val item = Item("test")
        val listItem = ListItem(item)
        listItem.id shouldBe item.id
    }
})
