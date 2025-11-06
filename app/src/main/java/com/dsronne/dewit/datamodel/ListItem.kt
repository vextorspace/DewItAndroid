package com.dsronne.dewit.datamodel

/**
 * Represents a list item that wraps an [Item].
 * By default, the item data has a label of "new item".
 */
data class ListItem(
    var data: Item = Item("new item")
) {
    fun label(): String = data.label

    val hasContent: Boolean get() = data is TextItem

    val id: ItemId
        get() = data.id

    val children: MutableList<ItemId> = mutableListOf()

    val workflows: List<Workflow>
        get() = data.workflows

    fun add(child: ListItem) {
        children.add(child.id)
    }

    fun addWorkflow(workflow: Workflow) {
        data.addWorkflow(workflow)
    }
}
