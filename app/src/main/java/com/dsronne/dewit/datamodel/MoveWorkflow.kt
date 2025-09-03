package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

class MoveWorkflow(targetId: ItemId) : CopyWorkflow(targetId) {

    override fun name(): String {
        return "Move -> ${targetId.id}"
    }

    override fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem) : Boolean {
        if(! super.apply(itemStore, parentId, item)) return false

        return removeFromOldParent(itemStore, parentId, item)
    }

    private fun removeFromOldParent(
        itemStore: ItemStore,
        parentId: ItemId,
        item: ListItem
    ): Boolean {
        itemStore.find(parentId)?.let { oldParent ->
            oldParent.children.remove(item.id)
            itemStore.edit(oldParent)
            println("Removed ${item.label()} from ${parentId}")
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MoveWorkflow
        return targetId == other.targetId
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
