package com.dsronne.testdewit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
    @Test
    fun itemsWithSameIdAreEqual() {
        val id = "shared-id"
        val item1 = Item("first", id)
        val item2 = Item("second", id)
        assertEquals(item1, item2)
    }

    @Test
    fun itemsCreatedWithSameLabelHaveDifferentIds() {
        val label = "same-label"
        val item1 = Item(label)
        val item2 = Item(label)
        assertNotEquals(item1.id, item2.id)
    }
}
