package com.dsronne.testdewit

/**
 * A store that manages ListItems and allows finding them by their item IDs.
 */
class ItemStore(items: List<ListItem>) {
    private val _items = items.toMutableList()

    /**
     * Finds a ListItem by its item's ID.
     * @param id The ID of the item to find
     * @return The matching ListItem or null if not found
     */
    fun find(id: String): ListItem? {
        return _items.find { it.id == id }
    }

    /**
     * Adds a new ListItem to the store.
     * @param item The ListItem to add
     */
    fun add(item: ListItem) {
        _items.add(item)
    }
}