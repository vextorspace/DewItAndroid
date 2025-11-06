package com.dsronne.dewit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dsronne.dewit.R
import com.dsronne.dewit.datamodel.ListItem

class ChildrenAdapter(
    private val onChildClicked: (ListItem) -> Unit,
    private val onChildRemoved: (ListItem) -> Unit
) : RecyclerView.Adapter<ChildrenAdapter.ChildViewHolder>() {
    private val items = mutableListOf<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child_row, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(children: List<ListItem>) {
        items.clear()
        items.addAll(children)
        notifyDataSetChanged()
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val labelView: TextView = itemView.findViewById(R.id.text_child_label)
        private val removeButton: ImageButton = itemView.findViewById(R.id.button_remove_child)

        fun bind(item: ListItem) {
            labelView.text = item.label()
            itemView.setOnClickListener { onChildClicked(item) }
            removeButton.setOnClickListener {
                onChildRemoved(item)
            }
        }
    }
}
