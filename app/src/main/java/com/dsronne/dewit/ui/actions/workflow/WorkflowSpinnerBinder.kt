package com.dsronne.dewit.ui.actions.workflow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.dsronne.dewit.R
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.tree.TreeModel.TreeNode

/**
 * Encapsulates all logic for rendering and handling the workflow spinner.
 * TreeAdapter delegates spinner view setup and interactions to this binder.
 */
class WorkflowSpinnerBinder(private val itemStore: ItemStore) {

    fun bind(spinner: Spinner, node: TreeNode, onApplied: () -> Unit) {
        val workflows = itemStore.getWorkflows(node.path)

        if (workflows.isEmpty()) {
            spinner.visibility = View.GONE
            return
        }

        spinner.visibility = View.VISIBLE
        val placeholder = "dewit..."
        val wfNames = listOf(placeholder) + workflows.map { it.name() }
        val wfAdapter = ArrayAdapter(
            spinner.context,
            R.layout.spinner_item_workflow,
            wfNames
        )
        wfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = wfAdapter
        spinner.setSelection(0, false)
        setSpinnerWidthToMin(spinner)
        // setSpinnerWidthToText(spinner, placeholder)

        var lastPos = 0
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == lastPos || position == 0) return
                lastPos = position
                val workflow = workflows[position - 1]
                val parentPath = node.path.parent()
                val parentId = parentPath[parentPath.size() - 1]
                if (workflow.apply(itemStore, parentId, node.item)) {
                    onApplied()
                    spinner.setSelection(0)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setSpinnerWidthToText(spinner: Spinner, text: String) {
        val tv = LayoutInflater.from(spinner.context)
            .inflate(R.layout.spinner_item_workflow, null, false) as TextView
        tv.text = text
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        tv.measure(widthSpec, heightSpec)
        val arrowExtraPx = dpToPx(spinner.context, 50f)
        val targetWidth = tv.measuredWidth + arrowExtraPx
        val lp = spinner.layoutParams
        if (lp.width != targetWidth) {
            lp.width = targetWidth
            spinner.layoutParams = lp
        }
    }

    private fun setSpinnerWidthToMin(spinner: Spinner) {
        val arrowExtraPx = dpToPx(spinner.context, 50f)
        val lp = spinner.layoutParams
        if (lp.width != arrowExtraPx) {
            lp.width = arrowExtraPx
            spinner.layoutParams = lp
        }
    }

    private fun dpToPx(ctx: Context, dp: Float): Int = (dp * ctx.resources.displayMetrics.density).toInt()
}

