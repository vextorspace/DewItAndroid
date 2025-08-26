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
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.storage.ItemStore
import android.widget.EditText
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import com.dsronne.dewit.datamodel.ItemId

/**
 * A simple tree-capable RecyclerView adapter for displaying nested ListItems.
 */
class TreeAdapter(
    private val itemStore: ItemStore,
    private val rootItem: ListItem
) : RecyclerView.Adapter<TreeAdapter.TreeViewHolder>() {
    private val nodes = mutableListOf<TreeNode>()

    data class TreeNode(val item: ListItem, val depth: Int, val path: Path, var isExpanded: Boolean = true)

    init {
        buildInitialNodes()
    }

    private fun buildInitialNodes() {
        nodes.clear()
        // recursively add all child nodes expanded by default, tracking path from root
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tree_node, parent, false)
        return TreeViewHolder(view)
    }

    override fun getItemCount(): Int = nodes.size

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(nodes[position])
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

            val workflows = itemStore.getWorkflows(node.path)

            if (workflows.isEmpty()) {
                spinnerWorkflows.visibility = View.GONE
            } else {
                spinnerWorkflows.visibility = View.VISIBLE
                // add a placeholder at index 0
                val wfNames = listOf("dewit...") + workflows.map { it.name() }
                val wfAdapter = ArrayAdapter(
                    itemView.context,
                    android.R.layout.simple_spinner_item,
                    wfNames
                )
                wfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerWorkflows.adapter = wfAdapter
                // process workflows when a real selection is made (skip placeholder)
                var lastPos = 0
                spinnerWorkflows.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        if (position == lastPos || position == 0) return
                        lastPos = position
                        val workflow = workflows[position - 1]
                        val parentPath = node.path.parent()
                        val parentId = parentPath[parentPath.size() - 1]
                        if (workflow.apply(itemStore, parentId, node.item)) {
                            println("Workflow applied successfully, rebuilding")
                            rebuildTree()
                            // refresh spinner back to placeholder to update UI
                            spinnerWorkflows.setSelection(0)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
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
                if (node.isExpanded) collapseNode(adapterPosition)
                else expandNode(adapterPosition)
            }
            buttonAdd.setOnClickListener {
                val child = ListItem(Item("new item"))
                itemStore.add(child)
                node.item.add(child)
                itemStore.edit(node.item)
                rebuildTree()
            }
            buttonEdit.setOnClickListener {
                // inline edit in-place
                val row = itemView as ViewGroup
                // if already editing, commit
                val editIndex = (0 until row.childCount).firstOrNull { row.getChildAt(it) is EditText } ?: -1
                if (editIndex != -1) {
                    val existing = row.getChildAt(editIndex) as EditText
                    val newLabel = existing.text.toString()
                    node.item.data.label = newLabel
                    itemStore.edit(node.item)
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
                // remove this node from its parent (or root) and refresh
                val pos = bindingAdapterPosition
                if (node.depth == 0) {
                    // top-level: parent is the root item
                    rootItem.children.remove(node.item.id)
                    itemStore.edit(rootItem)
                } else {
                    // nested: find parent node above with depth one less
                    val parentIndex = (pos - 1 downTo 0).firstOrNull { nodes[it].depth == node.depth - 1 }
                    parentIndex?.let {
                        val parentNode = nodes[it]
                        parentNode.item.children.remove(node.item.id)
                        itemStore.edit(parentNode.item)
                    }
                }
                rebuildTree()
            }
        }
    }

    private fun expandNode(position: Int) {
        val node = nodes[position]
        node.isExpanded = true
        val children = itemStore.getChildrenOf(node.item.id)
        val depth = node.depth + 1
        val insertPosition = position + 1
        val newNodes = children.map { child -> TreeNode(child, depth, node.path + child.id) }
        nodes.addAll(insertPosition, newNodes)
        notifyItemRangeInserted(insertPosition, newNodes.size)
        notifyItemChanged(position)
    }

    private fun collapseNode(position: Int) {
        val node = nodes[position]
        node.isExpanded = false
        val removeCount = countDescendants(position)
        for (i in 0 until removeCount) {
            nodes.removeAt(position + 1)
        }
        notifyItemRangeRemoved(position + 1, removeCount)
        notifyItemChanged(position)
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

    fun rebuildTree() {
        buildInitialNodes()
        notifyDataSetChanged()
    }

    companion object {
        private const val INDENT_WIDTH = 40
    }
}
