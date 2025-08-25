package com.dsronne.dewit

import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.MoveWorkflow
import com.dsronne.dewit.storage.InMemoryItemRepository
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain

class WorkflowsForProjectManagementTest: BehaviorSpec({
    Given("project management") {
        val itemStore = ItemStore(InMemoryItemRepository())
        itemStore.initProgramManagement()

        When("workflows for inbox checked") {
            val workflows = itemStore.find(ItemId("inbox"))!!.workflows
            println("hi" + workflows.size)
            Then("it should contain move to projects") {
                workflows shouldContain MoveWorkflow(ItemId("projects"))
            }

            And("It should contain move to references") {
                workflows shouldContain MoveWorkflow(ItemId("references"))
            }
            And("It should contain move to todo") {
                workflows shouldContain MoveWorkflow(ItemId("todo"))
            }
            And("It should contain move to waiting") {
                workflows shouldContain MoveWorkflow(ItemId("waiting"))
            }
            And("It should contain move to someday") {
                workflows shouldContain MoveWorkflow(ItemId("someday"))
            }
        }
    }
})