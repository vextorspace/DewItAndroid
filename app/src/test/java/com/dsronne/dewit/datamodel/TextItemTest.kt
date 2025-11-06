package com.dsronne.dewit.datamodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TextItemTest: FunSpec({
    test("Default TextItem has empty string content") {
        val item = TextItem("Test Item");
        item.content shouldBe ""
    }

})