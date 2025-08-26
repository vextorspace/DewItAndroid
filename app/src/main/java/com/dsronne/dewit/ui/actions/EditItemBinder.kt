package com.dsronne.dewit.ui.actions

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.ui.tree.TreeModel
import com.dsronne.dewit.ui.tree.TreeModel.TreeNode

/**
 * Encapsulates inline edit behavior for a tree row.
 */
class EditItemBinder(private val model: TreeModel) {

    fun bind(
        button: ImageButton,
        viewHolder: RecyclerView.ViewHolder,
        row: ViewGroup,
        labelView: TextView,
        node: TreeNode,
        onUpdated: (TreeModel.Change.Update) -> Unit
    ) {
        button.setOnClickListener {
            // If already editing, commit
            val editIndex = (0 until row.childCount).firstOrNull { row.getChildAt(it) is EditText } ?: -1
            if (editIndex != -1) {
                val existing = row.getChildAt(editIndex) as EditText
                commitEdit(viewHolder, row, labelView, existing, onUpdated)
                return@setOnClickListener
            }

            // Start editing
            startEditing(row, labelView, node) { et ->
                et.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) commitEdit(viewHolder, row, labelView, et, onUpdated)
                }
                et.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        commitEdit(viewHolder, row, labelView, et, onUpdated)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    fun beginEdit(
        viewHolder: RecyclerView.ViewHolder,
        row: ViewGroup,
        labelView: TextView,
        node: TreeNode,
        onUpdated: (TreeModel.Change.Update) -> Unit
    ) {
        startEditing(row, labelView, node) { et ->
            et.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) commitEdit(viewHolder, row, labelView, et, onUpdated)
            }
            et.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    commitEdit(viewHolder, row, labelView, et, onUpdated)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun startEditing(
        row: ViewGroup,
        labelView: TextView,
        node: TreeNode,
        configure: (EditText) -> Unit
    ) {
        val index = row.indexOfChild(labelView)
        if (index != -1) row.removeView(labelView)
        val editText = EditText(row.context).apply {
            setText(node.item.label())
            layoutParams = labelView.layoutParams
            setTextSize(TypedValue.COMPLEX_UNIT_PX, labelView.textSize)
            typeface = labelView.typeface
            requestFocus()
            setSelectAllOnFocus(true)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        row.addView(editText, if (index != -1) index else row.childCount)
        val imm = row.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        configure(editText)
    }

    private fun commitEdit(
        viewHolder: RecyclerView.ViewHolder,
        row: ViewGroup,
        labelView: TextView,
        editText: EditText,
        onUpdated: (TreeModel.Change.Update) -> Unit
    ) {
        val newLabel = editText.text.toString()
        val pos = viewHolder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        when (val change = model.updateLabel(pos, newLabel)) {
            is TreeModel.Change.Update -> onUpdated(change)
            else -> {}
        }
        val index = row.indexOfChild(editText)
        if (index != -1) row.removeViewAt(index)
        labelView.text = newLabel
        row.addView(labelView, if (index != -1) index else row.childCount)
        val imm = row.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(labelView.windowToken, 0)
    }
}
