package com.dsronne.testdewit

/**
 * Simple domain model representing an item with a text label.
 */
/**
 * Represents an item with a text label and an optional identifier.
 */
data class Item(
    val label: String,
    val id: String? = null
)
