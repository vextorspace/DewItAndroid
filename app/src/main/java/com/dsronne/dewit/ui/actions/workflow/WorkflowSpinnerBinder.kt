package com.dsronne.dewit.ui.actions.workflow

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.dsronne.dewit.R
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Path
import com.dsronne.dewit.storage.ItemStore

/**
 * Encapsulates all logic for rendering and handling the workflow spinner.
 * Delegates spinner view setup and interactions to this binder.
 */
class WorkflowSpinnerBinder(private val itemStore: ItemStore) {

    fun bind(
        spinner: Spinner,
        breadcrumb: List<ListItem>,
        onApplied: () -> Unit
    ) {
        if (breadcrumb.size <= 2) {
            hideSpinner(spinner)
            return
        }
        val pathItemIds = breadcrumb.map { it.id }
        val workflows = itemStore.getWorkflows(Path(pathItemIds))
        if (workflows.isEmpty()) {
            hideSpinner(spinner)
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

        val currentItem = breadcrumb.last()
        val parentId = breadcrumb[breadcrumb.size - 2].id

        var lastPos = 0
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == lastPos || position == 0) return
                lastPos = position
                val workflow = workflows[position - 1]
                if (workflow.apply(itemStore, parentId, currentItem)) {
                    onApplied()
                    spinner.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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

    private fun hideSpinner(spinner: Spinner) {
        spinner.visibility = View.GONE
        spinner.adapter = null
        spinner.onItemSelectedListener = null
    }
}
