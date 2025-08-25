package com.dsronne.dewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.InMemoryItemRepository
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain

class WorkflowInStoreTest : BehaviorSpec({
    Given("An item in a store and a workflow") {
        val itemStore = ItemStore(InMemoryItemRepository())
        val item = ListItem(Item("test"))
        val workflow = TestWorkflow("test")

        When("Added and stored") {
            itemStore.add(item)
            item.addWorkflow(workflow)
            itemStore.edit(item)

            Then("Item has workflow") {
                itemStore.find(item.id)!!.workflows shouldContain workflow
            }
        }
    }

    Given("An item in a store and two workflows") {
        val itemStore = ItemStore(InMemoryItemRepository())
        val item = ListItem(Item("test"))
        val workflow1 = TestWorkflow("test 1")
        val workflow2 = TestWorkflow("test 2")

        When("Added and stored") {
            itemStore.add(item)
            item.addWorkflow(workflow1)
            item.addWorkflow(workflow2)
            itemStore.edit(item)

            Then("Item has workflows") {
                itemStore.find(item.id)!!.workflows shouldContain workflow1
                itemStore.find(item.id)!!.workflows shouldContain workflow2

            }
        }
    }


})