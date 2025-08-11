package com.dsronne.testdewit.storage

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.viewports.ItemBrowser
import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.domain.ports.ItemRepository
import com.dsronne.testdewit.viewports.ItemEditor

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
            it.add(testItem)
            edit(it)
        }
    }
}
