package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Workflow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class ListItemWorkflowTest : io.kotest.core.spec.style.FunSpec({
    test("add workflow") {
        val item = ListItem(Item("test"))
        class TestWorkflow : Workflow
        val workflow = TestWorkflow()

        item.workflows.shouldBeEmpty()
        item.addWorkflow(workflow)
        item.workflows.shouldContainExactly(workflow)
    }
})
