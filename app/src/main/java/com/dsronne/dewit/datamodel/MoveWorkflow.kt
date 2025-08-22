package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

class MoveWorkflow(targetId: ItemId) : CopyWorkflow(targetId) {
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
            return true
        }
        return false
    }
}
