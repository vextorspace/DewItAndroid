package com.dsronne.testdewit

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Workflow
import com.dsronne.dewit.storage.InMemoryItemRepository
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

class MoveWorkflowTest: BehaviorSpec ({
    Given("A move, an item and an itemstore") {
        val moveFlow = MoveWorkflow(ItemId("projects"))
        val itemStore = ItemStore(InMemoryItemRepository())
        itemStore.initProgramManagement()
        val item = ListItem(Item("test"))
        val inbox = itemStore.find(ItemId("inbox"))
        inbox!!.add(item)

        When("Applied to an item in the itemstore") {
            moveFlow.apply(itemStore, inbox.id, item)

            Then("The item should appear in the new location") {
                val projects = itemStore.find(ItemId("projects"))
                projects!!.children shouldContain item.id
            }
            And("The original item should be no longer present") {
                inbox.children shouldNotContain item.id
            }
        }
    }
})

class MoveWorkflow(val targetId: ItemId) : Workflow {
    override fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem) {
        val targetItem = itemStore.find(targetId)

        targetItem?.let { targetItem ->
            targetItem.add(item)
            itemStore.edit(targetItem)
            itemStore.find(parentId)?.children?.remove(item.id)
        }
    }
}
