package com.dsronne.dewit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.R
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore
import android.widget.EditText
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import com.dsronne.dewit.ui.tree.TreeModel
import com.dsronne.dewit.ui.tree.TreeModel.TreeNode
import com.dsronne.dewit.ui.workflow.WorkflowSpinnerBinder

/**
 * A simple tree-capable RecyclerView adapter for displaying nested ListItems.
 */
class TreeAdapter(
    private val itemStore: ItemStore,
    private val rootItem: ListItem
) : RecyclerView.Adapter<TreeAdapter.TreeViewHolder>() {
    private val model = TreeModel(itemStore, rootItem)
    private val workflowBinder = WorkflowSpinnerBinder(itemStore)

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
            buttonAdd.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                when (val change = model.addChildTo(pos)) {
                    is TreeModel.Change.Rebuild -> applyRebuild(change)
                    else -> {}
                }
            }
            buttonEdit.setOnClickListener {
                // inline edit in-place
                val row = itemView as ViewGroup
                // if already editing, commit
                val editIndex = (0 until row.childCount).firstOrNull { row.getChildAt(it) is EditText } ?: -1
                if (editIndex != -1) {
                    val existing = row.getChildAt(editIndex) as EditText
                    val newLabel = existing.text.toString()
                    val pos = bindingAdapterPosition
                    if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                    when (val change = model.updateLabel(pos, newLabel)) {
                        is TreeModel.Change.Update -> notifyItemChanged(change.position)
                        else -> {}
                    }
                    row.removeViewAt(editIndex)
                    labelView.text = newLabel
                    row.addView(labelView, editIndex)
                    val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(labelView.windowToken, 0)
                    return@setOnClickListener
                }
                // start editing
                val index = row.indexOfChild(labelView)
                row.removeView(labelView)
                val editText = EditText(itemView.context).apply {
                    setText(node.item.label())
                    layoutParams = labelView.layoutParams
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, labelView.textSize)
                    typeface = labelView.typeface
                    requestFocus()
                    setSelectAllOnFocus(true)
                    imeOptions = EditorInfo.IME_ACTION_DONE
                }
                row.addView(editText, index)
                val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                editText.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) buttonEdit.performClick()
                }
                editText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        buttonEdit.performClick()
                        true
                    } else {
                        false
                    }
                }
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
