package com.dsronne.testdewit

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class ListItemTest {
    @Test
    fun defaultConstructorProvidesDefaultData() {
        val listItem = ListItem()
        assertNotNull("Expected default data to be non-null", listItem.data)
        assertNotNull(
            "Expected default data to be an Item instance",
            listItem.data
        )
    }

    @Test
    fun addChildAddsChildId() {
        val parent = ListItem()
        val child = ListItem()
        parent.add(child)
        assertTrue(
            "Expected children to contain the added child's id",
            parent.children.contains(child.id)
        )
    }

    @Test
    fun labelReturnsItemLabel() {
        val label = "test label"
        val item = Item(label)
        val listItem = ListItem(item)
        assertEquals(
            "Expected label() to return the item's label",
            label,
            listItem.label()
        )
    }

    @Test
    fun addChildrenIdsOrderIsPreserved() {
        val parent = ListItem()
        val firstChild = ListItem()
        val secondChild = ListItem()
        parent.add(firstChild)
        parent.add(secondChild)
        assertEquals(
            "Expected second child's id to be last in children list",
            secondChild.id,
            parent.children.last()
        )
    }

    @Test
    fun idReturnsItemId() {
        val item = Item("test")
        val listItem = ListItem(item)
        assertEquals("listItem's id should be the same as items", item.id, listItem.id)
    }
}