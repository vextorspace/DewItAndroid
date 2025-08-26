package com.dsronne.dewit.ui.tree

import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.storage.ItemStore

/**
 * TreeModel holds the flattened tree representation and encapsulates
 * expand/collapse and mutation operations. The adapter observes and renders it.
 */
class TreeModel(
    private val itemStore: ItemStore,
    private val rootItem: ListItem
) {
    data class TreeNode(val item: ListItem, val depth: Int, val path: Path, var isExpanded: Boolean = true)

    val nodes: MutableList<TreeNode> = mutableListOf()

    sealed class Change {
        data class Insert(val position: Int, val count: Int) : Change()
        data class Remove(val position: Int, val count: Int) : Change()
        data class Update(val position: Int) : Change()
        data class Rebuild(val oldSize: Int, val newSize: Int) : Change()
        object None : Change()
    }

    init {
        buildInitialNodes()
    }

    private fun buildInitialNodes() {
        nodes.clear()
        fun addNodes(item: ListItem, depth: Int, path: Path) {
            nodes.add(TreeNode(item, depth, path, isExpanded = true))
            item.children.mapNotNull { itemStore.find(it) }.forEach { child ->
                addNodes(child, depth + 1, path + child.id)
            }
        }
        val rootPath = Path.root() + rootItem.id
        rootItem.children.mapNotNull { itemStore.find(it) }.forEach { child ->
            addNodes(child, 0, rootPath + child.id)
        }
    }

    fun expandNode(position: Int): Change {
        val node = nodes[position]
        if (node.isExpanded) return Change.None
        node.isExpanded = true
        val children = itemStore.getChildrenOf(node.item.id)
        val depth = node.depth + 1
        val insertPosition = position + 1
        val newNodes = children.map { child -> TreeNode(child, depth, node.path + child.id) }
        nodes.addAll(insertPosition, newNodes)
        return Change.Insert(insertPosition, newNodes.size).also { /* caller should notify */ }
    }

    fun collapseNode(position: Int): Change {
        val node = nodes[position]
        if (!node.isExpanded) return Change.None
        node.isExpanded = false
        val removeCount = countDescendants(position)
        repeat(removeCount) { nodes.removeAt(position + 1) }
        return Change.Remove(position + 1, removeCount)
    }

    private fun countDescendants(position: Int): Int {
        val startDepth = nodes[position].depth
        var count = 0
        for (i in position + 1 until nodes.size) {
            if (nodes[i].depth <= startDepth) break
            count++
        }
        return count
    }

    fun rebuild(): Change.Rebuild {
        val oldSize = nodes.size
        buildInitialNodes()
        return Change.Rebuild(oldSize, nodes.size)
    }

    fun addChildTo(position: Int): Change {
        val node = nodes[position]
        val newChild = com.dsronne.dewit.datamodel.ListItem(com.dsronne.dewit.datamodel.Item("new item"))
        itemStore.add(newChild)
        node.item.add(newChild)
        itemStore.edit(node.item)
        return rebuild()
    }

    fun removeAt(position: Int): Change {
        val node = nodes[position]
        if (node.depth == 0) {
            rootItem.children.remove(node.item.id)
            itemStore.edit(rootItem)
        } else {
            val parentIndex = (position - 1 downTo 0).firstOrNull { nodes[it].depth == node.depth - 1 }
            parentIndex?.let {
                val parentNode = nodes[it]
                parentNode.item.children.remove(node.item.id)
                itemStore.edit(parentNode.item)
            }
        }
        return rebuild()
    }

    fun updateLabel(position: Int, newLabel: String): Change {
        val node = nodes[position]
        node.item.data.label = newLabel
        itemStore.edit(node.item)
        return Change.Update(position)
    }
}

