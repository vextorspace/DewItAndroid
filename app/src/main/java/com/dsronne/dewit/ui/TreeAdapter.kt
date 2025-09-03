package com.dsronne.dewit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.R
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.tree.TreeModel
import com.dsronne.dewit.ui.tree.TreeModel.TreeNode
import com.dsronne.dewit.ui.actions.workflow.WorkflowSpinnerBinder
import com.dsronne.dewit.ui.actions.AddChildBinder
import com.dsronne.dewit.ui.actions.EditItemBinder
import com.dsronne.dewit.ui.actions.RemoveItemBinder
import com.dsronne.dewit.ui.actions.ExpandCollapseBinder
import com.dsronne.dewit.ui.actions.PasteItemBinder
import com.dsronne.dewit.ui.config.UiConfig
import com.dsronne.dewit.datamodel.ItemId

/**
 * A simple tree-capable RecyclerView adapter for displaying nested ListItems.
 */
class TreeAdapter(
    private val itemStore: ItemStore,
    private val rootItem: ListItem
) : RecyclerView.Adapter<TreeAdapter.TreeViewHolder>() {
    private val model = TreeModel(itemStore, rootItem)
    private val workflowBinder = WorkflowSpinnerBinder(itemStore)
    private val addChildBinder = AddChildBinder(model)
    private val editItemBinder = EditItemBinder(model)
    private val removeItemBinder = RemoveItemBinder(model)
    private val expandCollapseBinder = ExpandCollapseBinder(model)
    private val pasteItemBinder = PasteItemBinder(itemStore, model)
    private var pendingEditItemId: ItemId? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tree_node, parent, false)
        return TreeViewHolder(view)
    }

    override fun getItemCount(): Int = model.nodes.size

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(model.nodes[position])
    }

    fun positionOf(id: ItemId): Int = model.nodes.indexOfFirst { it.item.id == id }

    inner class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val indentView: View = itemView.findViewById(R.id.indent_view)
        private val labelView: TextView = itemView.findViewById(R.id.text_label)
        private val buttonExpand: ImageButton = itemView.findViewById(R.id.button_expand_collapse)
        private val buttonAdd: ImageButton = itemView.findViewById(R.id.button_add_child)
        private val buttonPaste: ImageButton = itemView.findViewById(R.id.button_paste_child)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.button_edit_item)
        private val buttonRemove: ImageButton = itemView.findViewById(R.id.button_remove_item)
        private val spinnerWorkflows: Spinner = itemView.findViewById(R.id.spinner_workflows)

        fun bind(node: TreeNode) {
            labelView.text = node.item.label()
            val params = indentView.layoutParams
            params.width = node.depth * UiConfig.INDENT_WIDTH_PX
            indentView.layoutParams = params

            workflowBinder.bind(spinnerWorkflows, node) {
                rebuildTree()
            }
            val children = itemStore.getChildrenOf(node.item.id)
            expandCollapseBinder.bind(
                buttonExpand,
                this,
                hasChildren = children.isNotEmpty(),
                isExpanded = node.isExpanded
            ) { change, pos ->
                when (change) {
                    is TreeModel.Change.Insert -> {
                        notifyItemRangeInserted(change.position, change.count)
                        notifyItemChanged(pos)
                    }
                    is TreeModel.Change.Remove -> {
                        notifyItemRangeRemoved(change.position, change.count)
                        notifyItemChanged(pos)
                    }
                    else -> {}
                }
            }
            addChildBinder.bind(buttonAdd, this) { change, newId ->
                pendingEditItemId = newId
                applyRebuild(change)
            }
            pasteItemBinder.bind(buttonPaste, this, { node.item.id }) { change ->
                applyRebuild(change)
            }
            editItemBinder.bind(buttonEdit, this, itemView as ViewGroup, labelView, node) { change ->
                notifyItemChanged(change.position)
            }
            removeItemBinder.bind(buttonRemove, this) { change -> applyRebuild(change) }
            // Auto-enter edit mode on newly added item
            if (pendingEditItemId != null && pendingEditItemId == node.item.id) {
                // Start editing immediately
                editItemBinder.beginEdit(
                    this,
                    itemView as ViewGroup,
                    labelView,
                    node
                ) { change -> notifyItemChanged(change.position) }
                // Smooth scroll into view afterwards
                (itemView.parent as? RecyclerView)?.post {
                    val pos = bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        (itemView.parent as? RecyclerView)?.smoothScrollToPosition(pos)
                    }
                }
                pendingEditItemId = null
            }
        }
    }

    fun rebuildTree() {
        when (val change = model.rebuild()) {
            is TreeModel.Change.Rebuild -> applyRebuild(change)
            else -> {}
        }
    }

    fun rebuildTreeAndFocusEdit(targetId: ItemId) {
        pendingEditItemId = targetId
        rebuildTree()
    }

    private fun applyRebuild(change: TreeModel.Change.Rebuild) {
        if (change.oldSize > 0) notifyItemRangeRemoved(0, change.oldSize)
        if (model.nodes.isNotEmpty()) notifyItemRangeInserted(0, model.nodes.size)
    }

    companion object {}
}
