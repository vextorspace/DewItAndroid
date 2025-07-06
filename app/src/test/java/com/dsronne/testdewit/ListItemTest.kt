package com.dsronne.testdewit

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
}
