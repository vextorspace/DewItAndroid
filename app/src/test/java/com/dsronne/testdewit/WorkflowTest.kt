package com.dsronne.testdewit

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.datamodel.Path
import com.dsronne.testdewit.datamodel.Workflow
import com.dsronne.testdewit.storage.InMemoryItemRepository
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import com.dsronne.testdewit.storage.ItemStore
import org.junit.Test

class WorkflowTest {
    @Test
    fun `workflow of path is union of all workflows in path`() {
        val itemStore = ItemStore(InMemoryItemRepository())
        val parentItem = ListItem(Item("parent", ItemId("parent")))
        val childItem = ListItem(Item("child", ItemId("child")))
        val grandchildItem = ListItem(Item("grandchild", ItemId("grandchild")))
        childItem.add(grandchildItem)
        parentItem.add(childItem)
        childItem.add(parentItem)

        class TestWorkflow(val name: String) : Workflow {

        }
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

        val path =
            Path(listOf(ItemId("root"), ItemId("parent"), ItemId("child"), ItemId("grandchild")))
        itemStore.getWorkflows(path) shouldContainExactlyInAnyOrder listOf(workflow1, workflow2)
    }

    @Test
    fun `child workflow supercedes parent workflow if same id`() {

    }
}
