package com.dsronne.testdewit

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifies that a ListItem created without arguments has a default data field of type Item.
 */
class ListItemTest {
    @Test
    fun defaultConstructorProvidesDefaultData() {
        val listItem = ListItem()
        assertNotNull("Expected default data to be non-null", listItem.data)
        assertTrue(
            "Expected default data to be an Item instance",
            listItem.data is Item
        )
    }

    @Test
    fun addChildAddsToChildren() {
        val parent = ListItem()
        val child = ListItem()
        parent.add(child)
        assertTrue(
            "Expected children to contain the added child",
            parent.children.contains(child)
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
    fun addChildrenOrderIsPreserved() {
        val parent = ListItem()
        val firstChild = ListItem()
        val secondChild = ListItem()
        parent.add(firstChild)
        parent.add(secondChild)
        assertEquals(
            "Expected second child to be last in children list",
            secondChild,
            parent.children.last()
        )
    }
}
