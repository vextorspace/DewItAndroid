package com.dsronne.testdewit

/**
 * Represents a list item that wraps an [Item].
 * By default, the item data has a label of "new item".
 */
data class ListItem(
    val data: Item = Item("new item")
)
