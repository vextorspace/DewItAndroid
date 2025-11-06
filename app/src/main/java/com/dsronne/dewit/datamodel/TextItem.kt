package com.dsronne.dewit.datamodel

class TextItem(
    label: String,
    var content: String = "",
    id: ItemId = ItemId(),
    workflows: MutableList<Workflow> = mutableListOf()
) : Item(label, id, workflows) {
    constructor(item: Item) : this(item.label, "", item.id, item.workflows)
}