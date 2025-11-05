package com.dsronne.dewit.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.dsronne.dewit.datamodel.ListItem

class BreadcrumbTrailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val container: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }
    private val selectableBackgroundResId: Int = context.obtainStyledAttributes(
        intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
    ).let { typedArray ->
        try {
            typedArray.getResourceId(0, 0)
        } finally {
            typedArray.recycle()
        }
    }

    init {
        isHorizontalScrollBarEnabled = false
        if (childCount == 0) addView(container)
    }

    fun render(path: List<ListItem>, onCrumbClick: (ListItem) -> Unit) {
        container.removeAllViews()
        if (path.isEmpty()) return
        val density = resources.displayMetrics.density
        val crumbPaddingHorizontal = (12 * density).toInt()
        val crumbPaddingVertical = (6 * density).toInt()
        val separatorPadding = (6 * density).toInt()
        path.forEachIndexed { index, item ->
            if (index > 0) {
                val separator = TextView(context).apply {
                    text = ">"
                    setPadding(separatorPadding, 0, separatorPadding, 0)
                }
                container.addView(separator)
            }
            val crumb = TextView(context).apply {
                text = item.label()
                setPadding(
                    crumbPaddingHorizontal,
                    crumbPaddingVertical,
                    crumbPaddingHorizontal,
                    crumbPaddingVertical
                )
                setTypeface(
                    typeface,
                    if (index == path.lastIndex) Typeface.BOLD else Typeface.NORMAL
                )
                isClickable = true
                isFocusable = true
                if (selectableBackgroundResId != 0) {
                    setBackgroundResource(selectableBackgroundResId)
                }
                setOnClickListener { onCrumbClick(item) }
            }
            container.addView(crumb)
        }
        post { fullScroll(View.FOCUS_RIGHT) }
    }
}
