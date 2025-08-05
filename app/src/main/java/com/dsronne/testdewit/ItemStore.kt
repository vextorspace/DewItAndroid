package com.dsronne.testdewit

/**
 * A store that manages ListItems and allows finding them by their item IDs.
 */
class ItemStore(private val items: List<ListItem>) {
    /**
     * Finds a ListItem by its item's ID.
     * @param id The ID of the item to find
     * @return The matching ListItem or null if not found
     */
    fun find(id: String): ListItem? {
        return items.find { it.id == id }
    }
}
