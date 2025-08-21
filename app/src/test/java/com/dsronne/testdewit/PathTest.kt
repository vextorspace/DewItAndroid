package com.dsronne.dewit

import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.Path
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PathTest : io.kotest.core.spec.style.FunSpec({
    test("empty path is root") {
        val path = Path()
        path[0] shouldBe ItemId("root")
    }

    test("path from path and id is new path") {
        val path = Path(listOf(ItemId("root"), ItemId("first")))
        val newPath = path + ItemId("second")
        newPath[2] shouldBe ItemId("second")
        newPath.parent() shouldBe path
    }
})
