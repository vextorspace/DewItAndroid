package com.dsronne.dewit.ui.actions

import android.content.Context
import android.util.TypedValue
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore

/**
 * Wires Add/Edit/Remove actions for the root header row in ItemFragment.
 */
class RootHeaderBinder(private val itemStore: ItemStore) {

    fun bind(
        buttonAdd: ImageButton,
        buttonPaste: ImageButton,
        buttonEdit: ImageButton,
        buttonRemove: ImageButton,
        labelView: TextView,
        currentItem: ListItem,
        onChildAdded: (com.dsronne.dewit.datamodel.ItemId) -> Unit,
        onPasted: (com.dsronne.dewit.datamodel.ItemId) -> Unit,
        onRemoved: () -> Unit
    ) {
        // Use the label's actual parent container to avoid mismatches.
        val container = labelView.parent as? ViewGroup
            ?: throw IllegalStateException("Label view must have a ViewGroup parent")
        buttonAdd.setOnClickListener {
            val child = itemStore.addChild(currentItem)
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
                isSingleLine = true
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
            editText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                ) {
                    buttonEdit.performClick()
                    true
                } else false
            }
        }

        // Paste last-removed item as child of current root page
        val hasClipboard = itemStore.lastRemoved() != null
        buttonPaste.isEnabled = hasClipboard
        buttonPaste.alpha = if (hasClipboard) 1f else 0.3f
        buttonPaste.setOnClickListener {
            val pasteId = itemStore.lastRemoved() ?: return@setOnClickListener
            if (pasteId == currentItem.id) return@setOnClickListener
            if (!currentItem.children.contains(pasteId)) {
                currentItem.children.add(pasteId)
                itemStore.edit(currentItem)
            }
            itemStore.clearRemoved()
            buttonPaste.isEnabled = false
            buttonPaste.alpha = 0.3f
            onPasted(pasteId)
        }

        buttonRemove.setOnClickListener {
            val root = itemStore.root()
            root.children.remove(currentItem.id)
            itemStore.edit(root)
            itemStore.rememberRemoved(currentItem.id)
            onRemoved()
        }
    }
}
