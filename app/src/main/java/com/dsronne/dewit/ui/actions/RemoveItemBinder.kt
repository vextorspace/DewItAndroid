package com.dsronne.dewit.ui.actions

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.ui.tree.TreeModel

/**
 * Encapsulates the remove-item button behavior for a tree node row.
 */
class RemoveItemBinder(private val model: TreeModel) {

    fun bind(
        button: ImageButton,
        viewHolder: RecyclerView.ViewHolder,
        onRebuild: (TreeModel.Change.Rebuild) -> Unit
    ) {
        button.setOnClickListener { _: View ->
            val pos = viewHolder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            when (val change = model.removeAt(pos)) {
                is TreeModel.Change.Rebuild -> onRebuild(change)
                else -> {}
            }
        }
    }
}

