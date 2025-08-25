package com.dsronne.dewit

import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Workflow
import com.dsronne.dewit.storage.ItemStore

class TestWorkflow(val name: String) : Workflow {
    override fun name(): String {
        return "Test Workflow $name"
    }

    override fun apply(
        itemStore: ItemStore,
        parentId: ItemId,
        item: ListItem
    ) : Boolean {
        return true
    }
}
