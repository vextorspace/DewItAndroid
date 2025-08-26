package com.dsronne.dewit.storage

import com.dsronne.dewit.datamodel.CopyWorkflow
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
    private val listeners: MutableList<() -> Unit> = mutableListOf()
    private var lastRemovedItemId: ItemId? = null
    init {
        items.forEach { repository.save(it) }
    }

    override fun add(newItem: ListItem) {
        repository.save(newItem)
        notifyChanged()
    }

    override fun edit(changedItem: ListItem) {
        repository.update(changedItem)
        notifyChanged()
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

    fun initProgramManagement() {
        val categories = listOf("inbox", "todo", "projects", "waiting", "someday", "references")

        val rootItem = root()
        categories.forEach { label ->
            val child = ListItem(Item(label, ItemId(label)))
            add(child)
            rootItem.add(child)
        }
        edit(rootItem)
        find(ItemId("inbox"))?.let {
            it.addWorkflow(MoveWorkflow(ItemId("projects")))
            it.addWorkflow(MoveWorkflow(ItemId("references")))
            it.addWorkflow(MoveWorkflow(ItemId("todo")))
            it.addWorkflow(MoveWorkflow(ItemId("waiting")))
            it.addWorkflow(MoveWorkflow(ItemId("someday")))
            edit(it)
        }

        find(ItemId("projects"))?.let {
            it.addWorkflow(MoveWorkflow(ItemId("references")))
            it.addWorkflow(CopyWorkflow(ItemId("todo")))
            it.addWorkflow(CopyWorkflow(ItemId("waiting")))
            it.addWorkflow(MoveWorkflow(ItemId("someday")))
            edit(it)
        }

        find(ItemId("someday"))?.let {
            it.addWorkflow(MoveWorkflow(ItemId("projects")))
            edit(it)
        }

        find(ItemId("waiting"))?.let {
            it.addWorkflow(CopyWorkflow(ItemId("todo")))
            edit(it)
        }

        find(ItemId("references"))?.let {
            it.addWorkflow(CopyWorkflow(ItemId("projects")))
            edit(it)
        }
    }

    fun getWorkflows(path: Path): List<Workflow> {
        if(path.isEmpty()) return emptyList()
        return path.itemIds().mapNotNull { id -> find(id) }.flatMap { it -> it.workflows }.distinct()
    }

        fun addChild(parent: ListItem, label: String = ""): ListItem {
        val child = ListItem(Item(label))
        add(child)
        parent.add(child)
        edit(parent)
        return child
    }

    fun rememberRemoved(itemId: ItemId) {
        lastRemovedItemId = itemId
        notifyChanged()
    }
    fun lastRemoved(): ItemId? = lastRemovedItemId
    fun clearRemoved() {
        lastRemovedItemId = null
        notifyChanged()
    }

    fun addChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyChanged() {
        val snapshot = listeners.toList()
        snapshot.forEach { it.invoke() }
    }
}
