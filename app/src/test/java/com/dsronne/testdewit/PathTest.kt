package com.dsronne.testdewit

import org.junit.Test
import org.junit.Assert.assertEquals

class PathTest {

    @Test
    fun `empty path is root`() {
        val path = Path()
        assertEquals(ItemId("root"), path[0])
    }
}