package com.dsronne.dewit.ui.actions

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.ui.tree.TreeModel

/**
 * Encapsulates expand/collapse behavior and icon/visibility handling.
 */
class ExpandCollapseBinder(private val model: TreeModel) {

    fun bind(
        button: ImageButton,
        viewHolder: RecyclerView.ViewHolder,
        hasChildren: Boolean,
        isExpanded: Boolean,
        onChange: (TreeModel.Change, Int) -> Unit
    ) {
        if (!hasChildren) {
            button.visibility = View.INVISIBLE
            button.setOnClickListener(null)
            return
        }

        button.visibility = View.VISIBLE
        button.setImageResource(
            if (isExpanded) android.R.drawable.arrow_up_float
            else android.R.drawable.arrow_down_float
        )

        button.setOnClickListener {
            val pos = viewHolder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val change = if (isExpanded) model.collapseNode(pos) else model.expandNode(pos)
            onChange(change, pos)
        }
    }
}

