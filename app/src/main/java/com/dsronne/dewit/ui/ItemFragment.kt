package com.dsronne.dewit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsronne.dewit.databinding.FragmentItemBinding
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.actions.RootHeaderBinder
import com.dsronne.dewit.ui.actions.workflow.WorkflowSpinnerBinder

/**
 * Fragment displaying an item and its direct children. Selecting a child drills into that item.
 */
class ItemFragment : Fragment() {

    private lateinit var itemStore: ItemStore
    private lateinit var currentItem: ListItem
    private lateinit var childrenAdapter: ChildrenAdapter
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    private var changeListener: (() -> Unit)? = null
    private var rootHeaderBinder: RootHeaderBinder? = null
    private lateinit var workflowSpinnerBinder: WorkflowSpinnerBinder
    private var headerContainer: ViewGroup? = null
    private var labelInsertionIndex: Int = -1
    private var pendingHeaderEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = requireActivity() as? ItemStoreProvider
            ?: throw IllegalStateException("Hosting activity must implement ItemStoreProvider")
        itemStore = provider.itemStore()
        val id = arguments?.getString(ARG_ITEM_ID)
            ?: throw IllegalStateException("Missing item id argument")
        currentItem = itemStore.find(ItemId(id))
            ?: throw IllegalStateException("Unknown item id: $id")
        workflowSpinnerBinder = WorkflowSpinnerBinder(itemStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRootView()
    }

    override fun onStart() {
        super.onStart()
        // Refresh the displayed item when store data changes anywhere.
        changeListener = {
            if (this::childrenAdapter.isInitialized) {
                itemStore.find(currentItem.id)?.let {
                    currentItem = it
                    renderCurrentItem()
                }
            }
        }
        itemStore.addChangeListener(changeListener!!)
    }

    override fun onStop() {
        super.onStop()
        changeListener?.let { itemStore.removeChangeListener(it) }
        changeListener = null
    }

    private fun bindRootView() {
        binding.childrenContainer.layoutManager = LinearLayoutManager(context)
        childrenAdapter = ChildrenAdapter { child ->
            showItem(child)
        }
        binding.childrenContainer.adapter = childrenAdapter
        headerContainer = (binding.textLabel.parent as? ViewGroup)?.also { parent ->
            labelInsertionIndex = parent.indexOfChild(binding.textLabel).coerceAtLeast(0)
        }
        rootHeaderBinder = RootHeaderBinder(itemStore)
        renderCurrentItem()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderCurrentItem() {
        ensureLabelRestored()
        binding.textLabel.text = currentItem.label()
        binding.textLabel.visibility = View.GONE
        binding.breadcrumbViewHeader.visibility = View.VISIBLE
        val targetId = currentItem.id
        rootHeaderBinder?.bind(
            buttonAdd = binding.buttonAddChild,
            buttonPaste = binding.buttonPasteChild,
            buttonEdit = binding.buttonEditItem,
            buttonRemove = binding.buttonRemoveItem,
            labelView = binding.textLabel,
            currentItem = currentItem,
            onChildAdded = { id ->
                pendingHeaderEdit = true
                itemStore.find(id)?.let { showItem(it) } ?: run {
                    refreshChildren()
                    triggerPendingHeaderEdit()
                }
            },
            onPasted = {
                refreshChildren()
            },
            onRemoved = {
                (activity as? RootPagerController)?.onRootChildRemoved(targetId)
            },
            onEditStateChanged = { editing ->
                binding.breadcrumbViewHeader.visibility = if (editing) View.GONE else View.VISIBLE
                binding.textLabel.visibility = if (editing) View.VISIBLE else View.GONE
            }
        )
        val breadcrumb = findBreadcrumbPath()
        val rootId = itemStore.root().id
        val displayBreadcrumb = breadcrumb.drop(1).let {
            if (it.isEmpty()) {
                if (currentItem.id == rootId) emptyList() else listOf(currentItem)
            } else it
        }
        binding.breadcrumbViewHeader.render(displayBreadcrumb) { showItem(it) }
        bindWorkflowSpinner(breadcrumb)
        refreshChildren()
        triggerPendingHeaderEdit()
    }

    private fun refreshChildren() {
        if (!this::childrenAdapter.isInitialized) return
        val children = itemStore.getChildrenOf(currentItem.id)
        childrenAdapter.submitList(children)
        val hasClipboard = itemStore.lastRemoved() != null
        binding.buttonPasteChild.isEnabled = hasClipboard
        binding.buttonPasteChild.alpha = if (hasClipboard) 1f else 0.3f
    }

    private fun showItem(item: ListItem) {
        currentItem = item
        renderCurrentItem()
    }

    private fun ensureLabelRestored() {
        val container = headerContainer ?: (binding.textLabel.parent as? ViewGroup)?.also {
            headerContainer = it
            labelInsertionIndex = it.indexOfChild(binding.textLabel).coerceAtLeast(0)
        } ?: return
        for (i in container.childCount - 1 downTo 0) {
            val child = container.getChildAt(i)
            if (child is EditText) {
                container.removeViewAt(i)
            }
        }
        if (binding.textLabel.parent !== container) {
            val targetIndex = labelInsertionIndex.coerceIn(0, container.childCount)
            container.addView(binding.textLabel, targetIndex)
        }
        binding.textLabel.visibility = View.GONE
        binding.breadcrumbViewHeader.visibility = View.VISIBLE
    }

    private fun findBreadcrumbPath(): List<ListItem> {
        val root = itemStore.root()
        val target = currentItem.id
        val visited = mutableSetOf<ItemId>()
        val path = mutableListOf<ListItem>()
        return findBreadcrumbPathRecursive(root, target, visited, path)
            ?: if (root.id == target) listOf(root) else listOf(currentItem)
    }

    private fun findBreadcrumbPathRecursive(
        node: ListItem,
        target: ItemId,
        visited: MutableSet<ItemId>,
        path: MutableList<ListItem>
    ): List<ListItem>? {
        if (!visited.add(node.id)) return null
        path.add(node)
        if (node.id == target) {
            return path.toList()
        }
        itemStore.getChildrenOf(node.id).forEach { child ->
            val result = findBreadcrumbPathRecursive(child, target, visited, path)
            if (result != null) {
                return result
            }
        }
        path.removeAt(path.lastIndex)
        visited.remove(node.id)
        return null
    }

    private fun bindWorkflowSpinner(breadcrumb: List<ListItem>) {
        if (!this::workflowSpinnerBinder.isInitialized) return
        workflowSpinnerBinder.bind(
            spinner = binding.spinnerWorkflowsCurrent,
            breadcrumb = breadcrumb,
            onApplied = { renderCurrentItem() }
        )
    }

    private fun triggerPendingHeaderEdit() {
        if (!pendingHeaderEdit) return
        pendingHeaderEdit = false
        binding.buttonEditItem.post { binding.buttonEditItem.performClick() }
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
