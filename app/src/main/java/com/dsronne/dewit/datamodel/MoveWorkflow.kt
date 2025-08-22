package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

class MoveWorkflow(val targetId: ItemId) : Workflow {
    override fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem) {
        val targetItem = itemStore.find(targetId)

        targetItem?.let { targetItem ->
            targetItem.add(item)
            itemStore.edit(targetItem)

            removeFromOldParent(itemStore, parentId, item)
        }
    }

    private fun removeFromOldParent(
        itemStore: ItemStore,
        parentId: ItemId,
        item: ListItem
    ): Unit? = itemStore.find(parentId)?.let { oldParent ->
        oldParent.children.remove(item.id)
        itemStore.edit(oldParent)
    }
}
