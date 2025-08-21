package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.datamodel.Workflow
import com.dsronne.dewit.storage.InMemoryItemRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import com.dsronne.dewit.storage.ItemStore

class WorkflowTest : io.kotest.core.spec.style.FunSpec({
    test("workflow of path is union of all workflows in path") {
        val itemStore = ItemStore(InMemoryItemRepository())
        val parentItem = ListItem(Item("parent", ItemId("parent")))
        val childItem = ListItem(Item("child", ItemId("child")))
        val grandchildItem = ListItem(Item("grandchild", ItemId("grandchild")))
        childItem.add(grandchildItem)
        parentItem.add(childItem)
        childItem.add(parentItem)

        class TestWorkflow(val name: String) : Workflow
        val workflow1 = TestWorkflow("workflow1")
        val workflow2 = TestWorkflow("workflow2")

        parentItem.addWorkflow(workflow1)
        grandchildItem.addWorkflow(workflow1)
        grandchildItem.addWorkflow(workflow2)

        itemStore.root().add(parentItem)
        itemStore.add(parentItem)
        itemStore.add(childItem)
        itemStore.add(grandchildItem)
        itemStore.edit(itemStore.root())

        val path = Path(listOf(ItemId("root"), ItemId("parent"), ItemId("child"), ItemId("grandchild")))
        itemStore.getWorkflows(path) shouldContainExactlyInAnyOrder listOf(workflow1, workflow2)
    }

    test("child workflow supercedes parent workflow if same id") {
        // TODO: implement
    }
})
