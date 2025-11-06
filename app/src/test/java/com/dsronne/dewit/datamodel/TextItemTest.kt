package com.dsronne.dewit.datamodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TextItemTest: FunSpec({
    test("Default TextItem has empty string content") {
        val item = TextItem("Test Item");
        item.content shouldBe ""
    }

    test("TextItem with content can act as Item") {
        val textItem = TextItem("Test Item", content = "Test Content");
        textItem.content shouldBe "Test Content"

        val item = textItem as Item
        item.label shouldBe textItem.label
        item.id shouldBe textItem.id
    }

    test("created from plain item has same label and id") {
        val item = Item("Test Item")
        val textItem = TextItem(item)
        textItem.label shouldBe item.label
        textItem.id shouldBe item.id
    }

})