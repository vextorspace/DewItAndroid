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

    @Test
    fun itemIdReturnsSecondConstructorValue() {
        val label = "label"
        val id = "identifier"
        val item = Item(label, id)
        assertEquals(id, item.id)
    }
}
