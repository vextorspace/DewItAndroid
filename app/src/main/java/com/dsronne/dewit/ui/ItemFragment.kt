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
import com.dsronne.dewit.ui.actions.RootHeaderBinder
import com.dsronne.dewit.ui.RootPagerController

/**
 * Fragment displaying an item and its nested children using a tree-capable RecyclerView adapter.
 */
class ItemFragment : Fragment() {

    private lateinit var itemStore: ItemStore
    private lateinit var currentItem: ListItem
    private lateinit var adapter: TreeAdapter
    private var changeListener: (() -> Unit)? = null
    private var rootHeaderBinder: RootHeaderBinder? = null

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

    override fun onStart() {
        super.onStart()
        // Rebuild this fragment's tree when store data changes anywhere.
        changeListener = { if (this::adapter.isInitialized) adapter.rebuildTree() }
        itemStore.addChangeListener(changeListener!!)
    }

    override fun onStop() {
        super.onStop()
        changeListener?.let { itemStore.removeChangeListener(it) }
        changeListener = null
    }

    private fun bindRootView(view: View) {
        val binding = FragmentItemBinding.bind(view)
        binding.textLabel.text = currentItem.label()
        rootHeaderBinder = RootHeaderBinder(itemStore).also { binder ->
            binder.bind(
                buttonAdd = binding.buttonAddChild,
                buttonEdit = binding.buttonEditItem,
                buttonRemove = binding.buttonRemoveItem,
                labelView = binding.textLabel,
                currentItem = currentItem,
                onChildrenChanged = { adapter.rebuildTree() },
                onRemoved = {
                    (activity as? RootPagerController)?.onRootChildRemoved(currentItem.id)
                }
            )
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
