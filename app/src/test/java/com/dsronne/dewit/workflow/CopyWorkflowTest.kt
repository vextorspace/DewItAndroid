package com.dsronne.dewit.workflow

import com.dsronne.dewit.datamodel.CopyWorkflow
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.InMemoryItemRepository
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain

class CopyWorkflowTest: BehaviorSpec( {
    Given("A move, an item and an itemstore") {
        val copyFlow = CopyWorkflow(ItemId("projects"))
        val itemStore = ItemStore(InMemoryItemRepository())
        itemStore.initProgramManagement()
        val item = ListItem(Item("test"))
        val inbox = itemStore.find(ItemId("inbox"))
        inbox!!.add(item)

        When("Applied to an item in the itemstore") {
            copyFlow.apply(itemStore, inbox.id, item)

            Then("The item should appear in the new location") {
                val projects = itemStore.find(ItemId("projects"))
                projects!!.children shouldContain item.id
            }
            And("The original item should still be present") {
                inbox.children shouldContain item.id
            }
        }
    }

})