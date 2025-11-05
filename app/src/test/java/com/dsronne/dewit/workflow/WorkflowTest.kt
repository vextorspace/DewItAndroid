package com.dsronne.dewit.workflow

import com.dsronne.dewit.datamodel.CopyWorkflow
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.MoveWorkflow
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.storage.InMemoryItemRepository
import com.dsronne.dewit.storage.ItemStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class WorkflowTest : BehaviorSpec({
    Given("workflows in items in store") {
        val itemStore = ItemStore(InMemoryItemRepository())
        val parentItem = ListItem(Item("parent", ItemId("parent")))
        val childItem = ListItem(Item("child", ItemId("child")))
        val grandchildItem = ListItem(Item("grandchild", ItemId("grandchild")))
        childItem.add(grandchildItem)
        parentItem.add(childItem)
        childItem.add(parentItem)

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

        When("workflows found for path") {
            val path = Path(
                listOf(
                    ItemId("root"),
                    ItemId("parent"),
                    ItemId("child"),
                    ItemId("grandchild")
                )
            )
            val flows = itemStore.getWorkflows(path)
            Then("all workflows for items in path should be there") {
                flows shouldContainExactlyInAnyOrder listOf(workflow1, workflow2)
            }
        }
    }

    Given("A move workflow") {
        val workflow1 = MoveWorkflow(ItemId("target1"))
        When("Compared to another with same target") {
            val result = MoveWorkflow(ItemId("target1")) == workflow1
            Then("They are equal") {
                result.shouldBeTrue()
            }
        }

        When("Compared to another with different target") {
            val result = MoveWorkflow(ItemId("target2")) == workflow1
            Then("They are not equal") {
                result.shouldBeFalse()
            }
        }

        When("Compared to a copy workflow with same target") {
            val result = CopyWorkflow(ItemId("target1")) == workflow1
            Then("They are not equal") {
                result.shouldBeFalse()
            }
        }
    }

    Given("A copy workflow") {
        val workflow1 = CopyWorkflow(ItemId("target1"))
        When("Compared to another with same target") {
            val result = CopyWorkflow(ItemId("target1")) == workflow1
            Then("They are equal") {
                result.shouldBeTrue()
            }
        }

        When("Compared to another with different target") {
            val result = CopyWorkflow(ItemId("target2")) == workflow1
            Then("They are not equal") {
                result.shouldBeFalse()
            }
        }
    }

})
