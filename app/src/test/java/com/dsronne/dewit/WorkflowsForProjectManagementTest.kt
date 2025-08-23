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

            Then("it should contain move to projects") {
                workflows shouldContain MoveWorkflow(ItemId("inbox"))
            }
        }
    }
})