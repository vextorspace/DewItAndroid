package com.dsronne.dewit.ui.actions

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore

/**
 * Wires Add/Edit/Remove actions for the root header row in ItemFragment.
 */
class RootHeaderBinder(private val itemStore: ItemStore) {

    fun bind(
        buttonAdd: ImageButton,
        buttonEdit: ImageButton,
        buttonRemove: ImageButton,
        labelView: TextView,
        currentItem: ListItem,
        onChildAdded: (com.dsronne.dewit.datamodel.ItemId) -> Unit,
        onRemoved: () -> Unit
    ) {
        // Use the label's actual parent container to avoid mismatches.
        val container = labelView.parent as? ViewGroup
            ?: throw IllegalStateException("Label view must have a ViewGroup parent")
        buttonAdd.setOnClickListener {
            val child = ListItem(Item("new item"))
            itemStore.add(child)
            currentItem.add(child)
            itemStore.edit(currentItem)
            onChildAdded(child.id)
        }

        buttonEdit.setOnClickListener {
            val editIndex = (0 until container.childCount).firstOrNull { container.getChildAt(it) is EditText } ?: -1
            if (editIndex != -1) {
                val existing = container.getChildAt(editIndex) as EditText
                val newLabel = existing.text.toString()
                currentItem.data.label = newLabel
                itemStore.edit(currentItem)
                container.removeViewAt(editIndex)
                labelView.text = newLabel
                container.addView(labelView, editIndex)
                val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(labelView.windowToken, 0)
                return@setOnClickListener
            }

            val index = container.indexOfChild(labelView)
            if (index != -1) container.removeView(labelView)
            val editText = EditText(container.context).apply {
                setText(currentItem.label())
                layoutParams = labelView.layoutParams
                setTextSize(TypedValue.COMPLEX_UNIT_PX, labelView.textSize)
                typeface = labelView.typeface
                requestFocus()
                setSelectAllOnFocus(true)
                imeOptions = EditorInfo.IME_ACTION_DONE
            }
            container.addView(editText, index)
            val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            val root = itemStore.root()
            root.children.remove(currentItem.id)
            itemStore.edit(root)
            onRemoved()
        }
    }
}
