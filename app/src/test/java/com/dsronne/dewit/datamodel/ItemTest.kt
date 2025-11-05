package com.dsronne.dewit.datamodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ItemTest : FunSpec({
    test("item label returns constructor value") {
        val text = "some string"
        val item = Item(text)
        item.label shouldBe text
    }

    test("item id returns second constructor value") {
        val label = "label"
        val id = ItemId("identifier")
        val item = Item(label, id)
        item.id shouldBe id
    }

    test("items with same id are equal") {
        val id = ItemId("shared-id")
        val item1 = Item("first", id)
        val item2 = Item("second", id)
        item1 shouldBe item2
    }

    test("items created with same label have different ids") {
        val label = "same-label"
        val item1 = Item(label)
        val item2 = Item(label)
        item1.id shouldNotBe item2.id
    }
})
