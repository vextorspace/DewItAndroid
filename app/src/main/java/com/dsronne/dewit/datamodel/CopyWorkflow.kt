package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

open class CopyWorkflow(val targetId: ItemId) : Workflow {
    override fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem) : Boolean {
        val targetItem = itemStore.find(targetId)

        targetItem?.let { targetItem ->
            targetItem.add(item)
            itemStore.edit(targetItem)
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CopyWorkflow
        return targetId == other.targetId
    }

    override fun hashCode(): Int {
        return targetId.hashCode()
    }
}
