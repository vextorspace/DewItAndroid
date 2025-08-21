package com.dsronne.testdewit

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.datamodel.Workflow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldBeEmpty
import org.junit.Test

class ListItemWorkflowTest {

    @Test
    fun `test add workflow`() {
        val item = ListItem(Item("test"))
        class TestWorkflow() : Workflow {}
        val workflow = TestWorkflow()

        item.workflows.shouldBeEmpty()
        item.addWorkflow(workflow)
        item.workflows.shouldContainExactly(workflow)
    }
}