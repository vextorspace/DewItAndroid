package com.dsronne.dewit.ui.actions

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.tree.TreeModel

/**
 * Handles paste action: attach the last-removed item as a child of the target node.
 */
class PasteItemBinder(private val itemStore: ItemStore, private val model: TreeModel) {

    fun bind(
        button: ImageButton,
        viewHolder: RecyclerView.ViewHolder,
        targetProvider: () -> ItemId,
        onRebuild: (TreeModel.Change.Rebuild) -> Unit
    ) {
        // Enable/disable based on clipboard presence
        button.isEnabled = itemStore.lastRemoved() != null

        button.setOnClickListener { _: View ->
            val pos = viewHolder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val pasteId = itemStore.lastRemoved() ?: return@setOnClickListener
            // Prevent self-link
            val targetId = targetProvider()
            if (pasteId == targetId) return@setOnClickListener

            val target = model.nodes.getOrNull(pos)?.item ?: return@setOnClickListener
            // Avoid duplicate link
            if (target.children.contains(pasteId)) return@setOnClickListener

            target.children.add(pasteId)
            itemStore.edit(target)
            when (val change = model.rebuild()) {
                is TreeModel.Change.Rebuild -> onRebuild(change)
                else -> {}
            }
        }
    }
}

