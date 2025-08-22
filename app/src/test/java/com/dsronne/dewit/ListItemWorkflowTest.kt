package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Workflow
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class ListItemWorkflowTest : io.kotest.core.spec.style.FunSpec({
    test("add workflow") {
        val item = ListItem(Item("test"))
        class TestWorkflow : Workflow {
            override fun apply(
                itemStore: ItemStore,
                parentId: ItemId,
                item: ListItem
            ) {
                TODO("Not yet implemented")
            }
        }

        val workflow = TestWorkflow()

        item.workflows.shouldBeEmpty()
        item.addWorkflow(workflow)
        item.workflows.shouldContainExactly(workflow)
    }
})
