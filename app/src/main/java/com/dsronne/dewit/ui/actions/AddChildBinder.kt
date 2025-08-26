package com.dsronne.dewit.ui.actions

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.ui.tree.TreeModel

/**
 * Encapsulates the add-child button behavior for a tree node row.
 */
class AddChildBinder(private val model: TreeModel) {

    fun bind(
        button: ImageButton,
        viewHolder: RecyclerView.ViewHolder,
        onRebuild: (TreeModel.Change.Rebuild, com.dsronne.dewit.datamodel.ItemId?) -> Unit
    ) {
        button.setOnClickListener { v: View ->
            val pos = viewHolder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            when (val change = model.addChildTo(pos)) {
                is TreeModel.Change.Rebuild -> onRebuild(change, model.lastAddedChildId)
                else -> {}
            }
        }
    }
}
