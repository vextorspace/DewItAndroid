package com.dsronne.testdewit

/**
 * Represents a list item that wraps an [Item].
 * By default, the item data has a label of "new item".
 */
data class ListItem(
    val data: Item = Item("new item")
)
{
    /**
     * Returns the wrapped item's label.
     */
    fun label(): String = data.label
    /**
     * Child list items nested under this item.
     */
    val children: MutableList<ListItem> = mutableListOf()

    /**
     * Adds a child list item under this item.
     */
    fun add(child: ListItem) {
        children.add(child)
    }
}
