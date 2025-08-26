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
import com.dsronne.dewit.ui.workflow.WorkflowSpinnerBinder
import com.dsronne.dewit.ui.actions.AddChildBinder
import com.dsronne.dewit.ui.actions.EditItemBinder

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tree_node, parent, false)
        return TreeViewHolder(view)
    }

    override fun getItemCount(): Int = model.nodes.size

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(model.nodes[position])
    }

    inner class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val indentView: View = itemView.findViewById(R.id.indent_view)
        private val labelView: TextView = itemView.findViewById(R.id.text_label)
        private val buttonExpand: ImageButton = itemView.findViewById(R.id.button_expand_collapse)
        private val buttonAdd: ImageButton = itemView.findViewById(R.id.button_add_child)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.button_edit_item)
        private val buttonRemove: ImageButton = itemView.findViewById(R.id.button_remove_item)
        private val spinnerWorkflows: Spinner = itemView.findViewById(R.id.spinner_workflows)

        fun bind(node: TreeNode) {
            labelView.text = node.item.label()
            val params = indentView.layoutParams
            params.width = node.depth * INDENT_WIDTH
            indentView.layoutParams = params

            workflowBinder.bind(spinnerWorkflows, node) {
                rebuildTree()
            }
            val children = itemStore.getChildrenOf(node.item.id)
            if (children.isEmpty()) {
                buttonExpand.visibility = View.INVISIBLE
            } else {
                buttonExpand.visibility = View.VISIBLE
                buttonExpand.setImageResource(
                    if (node.isExpanded) android.R.drawable.arrow_up_float
                    else android.R.drawable.arrow_down_float
                )
            }
            buttonExpand.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                if (node.isExpanded) {
                    when (val change = model.collapseNode(pos)) {
                        is TreeModel.Change.Remove -> {
                            notifyItemRangeRemoved(change.position, change.count)
                            notifyItemChanged(pos)
                        }
                        is TreeModel.Change.None -> {}
                        else -> {}
                    }
                } else {
                    when (val change = model.expandNode(pos)) {
                        is TreeModel.Change.Insert -> {
                            notifyItemRangeInserted(change.position, change.count)
                            notifyItemChanged(pos)
                        }
                        is TreeModel.Change.None -> {}
                        else -> {}
                    }
                }
            }
            addChildBinder.bind(buttonAdd, this) { change -> applyRebuild(change) }
            editItemBinder.bind(buttonEdit, this, itemView as ViewGroup, labelView, node) { change ->
                notifyItemChanged(change.position)
            }
            buttonRemove.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                when (val change = model.removeAt(pos)) {
                    is TreeModel.Change.Rebuild -> applyRebuild(change)
                    else -> {}
                }
            }
        }
    }

    fun rebuildTree() {
        when (val change = model.rebuild()) {
            is TreeModel.Change.Rebuild -> applyRebuild(change)
            else -> {}
        }
    }

    private fun applyRebuild(change: TreeModel.Change.Rebuild) {
        if (change.oldSize > 0) notifyItemRangeRemoved(0, change.oldSize)
        if (model.nodes.isNotEmpty()) notifyItemRangeInserted(0, model.nodes.size)
    }

    companion object {
        private const val INDENT_WIDTH = 40
    }
}
