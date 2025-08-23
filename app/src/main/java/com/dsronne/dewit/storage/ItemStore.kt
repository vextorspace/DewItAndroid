package com.dsronne.dewit.storage

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.viewports.ItemBrowser
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.MoveWorkflow
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.datamodel.Workflow
import com.dsronne.dewit.domain.ports.ItemRepository
import com.dsronne.dewit.viewports.ItemEditor

class ItemStore(
    private val repository: ItemRepository,
    items: List<ListItem> = emptyList()
) : ItemBrowser, ItemEditor {
    init {
        items.forEach { repository.save(it) }
    }

    override fun add(newItem: ListItem) {
        repository.save(newItem)
    }

    override fun edit(changedItem: ListItem) {
        repository.update(changedItem)
    }

    override fun find(id: ItemId): ListItem? {
        return repository.find(id)
    }

    override fun root(): ListItem {
        return find(ItemId("root")) ?: ListItem(Item("root", ItemId("root"))).also { add(it) }
    }

    override fun getChildrenOf(itemId: ItemId): List<ListItem> {
        val parent = find(itemId) ?: return emptyList()
        return parent.children.mapNotNull { childId -> find(childId) }
    }

    /**
     * Initializes the store with standard program management categories under the root:
     * inbox, todo, projects, waiting, someday, and references.
     */
    fun initProgramManagement() {
        val categories = listOf("inbox", "todo", "projects", "waiting", "someday", "references")

        val rootItem = root()
        categories.forEach { label ->
            val child = ListItem(Item(label, ItemId(label)))
            add(child)
            rootItem.add(child)
        }
        edit(rootItem)
        val inbox = find(ItemId("inbox"))
        val testItem = ListItem(Item("delete me"))
        add(testItem)
        inbox?.let {
            it.addWorkflow(MoveWorkflow(ItemId("projects")))
            it.add(testItem)
            edit(it)

        }
    }

    fun getWorkflows(path: Path): List<Workflow> {
        if(path.isEmpty()) return emptyList()
        return path.itemIds().mapNotNull { id -> find(id) }.flatMap { it -> it.workflows }.distinct()
    }
}
