package com.dsronne.dewit.datamodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class ListItemTest : FunSpec({
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

    test ("if item is regular item hascontent is false") {
        ListItem(Item("test"))
            .hasContent
            .shouldBeFalse();
    }

    test("if item is text item hascontent is true") {
        ListItem(TextItem("test", "content"))
            .hasContent
            .shouldBeTrue();
    }
})
