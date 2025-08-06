package com.dsronne.testdewit

import androidx.core.graphics.plus
import org.junit.Test
import org.junit.Assert.assertEquals

class PathTest {

    @Test
    fun `empty path is root`() {
        val path = Path()
        assertEquals(ItemId("root"), path[0])
    }

    @Test
    fun `Path from path and id is new path`() {
      val path = Path(listOf(ItemId("root"), ItemId("first")))
        val newPath = path + ItemId("second")
        assertEquals(ItemId("second"), newPath[2])
        assertEquals(path, newPath.parent())
    }
}