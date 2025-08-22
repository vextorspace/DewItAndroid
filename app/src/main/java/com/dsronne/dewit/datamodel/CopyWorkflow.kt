package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

class CopyWorkflow(val targetId: ItemId) : Workflow {
    override fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem) {
        val targetItem = itemStore.find(targetId)

        targetItem?.let { targetItem ->
            targetItem.add(item)
            itemStore.edit(targetItem)
        }
    }
}
