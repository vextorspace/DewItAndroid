package com.dsronne.dewit.datamodel

/**
 * Simple domain model representing an item with a text label.
 */
/**
 * Represents an item with a text label and an optional identifier.
 * Two items are considered equal if they share the same identifier.
 */

/**
 * Represents an item with a text label and a unique identifier.
 * By default, each instance is assigned a random UUID.
 * Two items are considered equal if they share the same identifier.
 */
open class Item(
    var label: String,
    val id: ItemId = ItemId(),
    val workflows: MutableList<Workflow> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Item) return false
        return id == other.id
    }
    fun addWorkflow(workflow: Workflow) {
        if (workflows.contains(workflow)) {
            return
        }
        workflows.add(workflow)
    }

    override fun hashCode(): Int = id.hashCode()
}
