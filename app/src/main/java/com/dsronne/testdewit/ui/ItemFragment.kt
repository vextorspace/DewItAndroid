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
import com.google.android.material.snackbar.Snackbar

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
        view.findViewById<TextView>(R.id.text_label).text = currentItem.label()
        val addButton = view.findViewById<ImageButton>(R.id.button_add_child)
        val childrenContainer = view.findViewById<LinearLayout>(R.id.children_container)

        fun createItemView(item: ListItem): View {
            val itemView = layoutInflater.inflate(R.layout.fragment_item, childrenContainer, false).also {
                // remove outer padding inherited from fragment_item root so nested items only indent on the left
                it.setPadding(0, 0, 0, 0)
            }
            val labelView = itemView.findViewById<TextView>(R.id.text_label)
            val addChildButton = itemView.findViewById<ImageButton>(R.id.button_add_child)
            val subContainer = itemView.findViewById<LinearLayout>(R.id.children_container)

            labelView.text = item.label()

            fun refreshSubItems() {
                subContainer.removeAllViews()
                itemStore.getChildrenOf(item.id).forEach { child ->
                    subContainer.addView(createItemView(child))
                }
            }

            addChildButton.setOnClickListener {
                val childItem = ListItem(Item("new item"))
                itemStore.add(childItem)
                item.add(childItem)
                itemStore.edit(item)
                Snackbar.make(itemView, "Added child '${childItem.label()}'", Snackbar.LENGTH_SHORT).show()
                refreshSubItems()
            }

            refreshSubItems()
            return itemView
        }

        addButton.setOnClickListener {
            val child = ListItem(Item("new item"))
            itemStore.add(child)
            currentItem.add(child)
            itemStore.edit(currentItem)
            Snackbar.make(view, "Added child '${child.label()}'", Snackbar.LENGTH_SHORT).show()
            childrenContainer.addView(createItemView(child))
        }

        itemStore.getChildrenOf(currentItem.id).forEach { child ->
            childrenContainer.addView(createItemView(child))
        }
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
}
