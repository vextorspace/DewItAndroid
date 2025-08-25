package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class ListItemWorkflowTest : BehaviorSpec({

    Given("a list item and a workflow") {
        val item = ListItem(Item("test"))
        val workflow = TestWorkflow("test 1")

        When("Not added to item") {
            Then("it is empty") {
                item.workflows.shouldBeEmpty()
            }
        }

        When("added to item") {
            item.addWorkflow(workflow)

            Then("It is in the workflows") {
                item.workflows.shouldContainExactly(workflow)
            }
        }
    }

    Given("a list item and two workflows") {
        val item = ListItem(Item("test"))
        val workflow1 = TestWorkflow("test 1")
        val workflow2 = TestWorkflow("test 2")
        When("both added to item") {
            item.addWorkflow(workflow1)
            item.addWorkflow(workflow2)

            Then("It is in the workflows") {
                item.workflows.shouldContainExactly(workflow1, workflow2)
            }
        }

        And("one added twice") {
            item.addWorkflow(workflow1)
            item.addWorkflow(workflow2)
            item.addWorkflow(workflow1)
            Then("it is once in the results") {
                item.workflows.shouldContainExactly(workflow1, workflow2)
            }
        }

    }
})
