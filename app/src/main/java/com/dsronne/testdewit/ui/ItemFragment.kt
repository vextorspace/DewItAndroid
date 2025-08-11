package com.dsronne.testdewit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.dsronne.testdewit.R
import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ListItem
import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.storage.ItemStore
import android.widget.EditText
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context

class ItemFragment(private val itemStore: ItemStore) : Fragment() {
    private lateinit var currentItem: ListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.getString(ARG_ITEM_ID)
            ?: throw IllegalStateException("Missing item id argument")
        currentItem = itemStore.find(ItemId(id))
            ?: throw IllegalStateException("Unknown item id: $id")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRootView(view)
    }

    private fun addChildTo(parent: ListItem, container: LinearLayout, hostView: View) {
        val child = ListItem(Item("new item"))
        itemStore.add(child)
        parent.add(child)
        itemStore.edit(parent)
        container.addView(createItemView(child, container, parent))
    }

    private fun createItemView(
        item: ListItem,
        parentContainer: LinearLayout,
        parentItem: ListItem
    ): View {
        val itemView = layoutInflater.inflate(R.layout.fragment_item, parentContainer, false).also {
            // remove outer padding inherited from fragment_item root so nested items only indent on the left
            it.setPadding(0, 0, 0, 0)
        }
        bindChildView(itemView, item, parentContainer, parentItem)
        return itemView
    }

    private fun commitEdit(
        row: ViewGroup,
        labelView: TextView,
        itemView: View,
        item: ListItem,
        newLabel: String,
        index: Int
    ) {
        item.data.label = newLabel
        itemStore.edit(item)
        row.removeViewAt(index)
        row.addView(labelView, index)
        labelView.text = newLabel
        val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(labelView.windowToken, 0)
    }

    companion object {
        private const val ARG_ITEM_ID = "arg_item_id"

        fun newInstance(item: ListItem, store: ItemStore): ItemFragment {
            val fragment = ItemFragment(store)
            fragment.arguments = Bundle().apply {
                putString(ARG_ITEM_ID, item.id.id)
            }
            return fragment
        }
    }

    private fun bindRootView(view: View) {
        val labelView = view.findViewById<TextView>(R.id.text_label)
        val addButton = view.findViewById<ImageButton>(R.id.button_add_child)
        val childrenContainer = view.findViewById<LinearLayout>(R.id.children_container)

        labelView.text = currentItem.label()
        addButton.setOnClickListener {
            addChildTo(currentItem, childrenContainer, view)
        }
        itemStore.getChildrenOf(currentItem.id).forEach { child ->
            childrenContainer.addView(createItemView(child, childrenContainer, currentItem))
        }
    }

    private fun bindChildView(
        itemView: View,
        item: ListItem,
        parentContainer: LinearLayout,
        parentItem: ListItem
    ) {
        val labelView = itemView.findViewById<TextView>(R.id.text_label)
        val addChildButton = itemView.findViewById<ImageButton>(R.id.button_add_child)
        val removeButton = itemView.findViewById<ImageButton>(R.id.button_remove_item)
        val editButton = itemView.findViewById<ImageButton>(R.id.button_edit_item)
        val subContainer = itemView.findViewById<LinearLayout>(R.id.children_container)

        labelView.text = item.label()

        addChildButton.setOnClickListener {
            addChildTo(item, subContainer, itemView)
        }

        editButton.setOnClickListener {
            // Toggle editing: if an EditText is already present, finish editing
            val row = (labelView.parent ?: editButton.parent) as ViewGroup
            val editIndex = (0 until row.childCount).firstOrNull { row.getChildAt(it) is EditText } ?: -1
            if (editIndex != -1) {
                val existingEditor = row.getChildAt(editIndex) as EditText
                val newLabel = existingEditor.text.toString()
                commitEdit(row, labelView, itemView, item, newLabel, editIndex)
                return@setOnClickListener
            }
            // Start editing
            val index = row.indexOfChild(labelView)
            row.removeView(labelView)
            val editText = EditText(itemView.context).apply {
                setText(item.label())
                layoutParams = labelView.layoutParams
                textSize = labelView.textSize / labelView.resources.displayMetrics.scaledDensity
                typeface = labelView.typeface
                requestFocus()
                setSelectAllOnFocus(true)
                imeOptions = EditorInfo.IME_ACTION_DONE
            }
            row.addView(editText, index)
            val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            editText.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) {
                    val newLabel = editText.text.toString()
                    commitEdit(row, labelView, itemView, item, newLabel, index)
                }
            }
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val newLabel = editText.text.toString()
                    commitEdit(row, labelView, itemView, item, newLabel, index)
                    true
                } else {
                    false
                }
            }
        }

        removeButton.setOnClickListener {
            parentItem.children.remove(item.id)
            itemStore.edit(parentItem)
            parentContainer.removeView(itemView)
        }

        itemStore.getChildrenOf(item.id).forEach { child ->
            subContainer.addView(createItemView(child, subContainer, item))
        }
    }
}
