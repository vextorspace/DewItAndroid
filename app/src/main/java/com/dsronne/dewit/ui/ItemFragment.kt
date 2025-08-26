package com.dsronne.dewit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsronne.dewit.databinding.FragmentItemBinding
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.TreeAdapter

/**
 * Fragment displaying an item and its nested children using a tree-capable RecyclerView adapter.
 */
class ItemFragment : Fragment() {

    private lateinit var itemStore: ItemStore
    private lateinit var currentItem: ListItem
    private lateinit var adapter: TreeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = requireActivity() as? ItemStoreProvider
            ?: throw IllegalStateException("Hosting activity must implement ItemStoreProvider")
        itemStore = provider.itemStore()
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
        val binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRootView(view)
    }

    private fun bindRootView(view: View) {
        val binding = FragmentItemBinding.bind(view)
        binding.textLabel.text = currentItem.label()
        binding.buttonAddChild.setOnClickListener {
            val child = ListItem(Item("new item"))
            itemStore.add(child)
            currentItem.add(child)
            itemStore.edit(currentItem)
            adapter.rebuildTree()
        }
        binding.childrenContainer.layoutManager =
            LinearLayoutManager(context)
        adapter = TreeAdapter(itemStore, currentItem)
        binding.childrenContainer.adapter = adapter
    }

    companion object {
        private const val ARG_ITEM_ID = "arg_item_id"

        fun newInstance(item: ListItem): ItemFragment = ItemFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM_ID, item.id.id)
            }
        }
    }
}
