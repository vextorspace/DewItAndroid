package com.dsronne.testdewit

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemTest {
    @Test
    fun itemLabelReturnsConstructorValue() {
        val text = "some string"
        val item = Item(text)
        assertEquals(text, item.label)
    }
}
