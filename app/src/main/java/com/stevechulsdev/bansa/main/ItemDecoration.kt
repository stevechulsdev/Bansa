package com.stevechulsdev.bansa.main

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(context: Context): RecyclerView.ItemDecoration() {
    private var spanCount: Int = 0
    private var spacing: Int = 0
    private var outerMargin: Int = 0

    init {
        spanCount = 2
        spacing = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, context.resources.displayMetrics)).toInt()
        outerMargin = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, context.resources.displayMetrics)).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val maxCount = parent.adapter?.itemCount
        val position = parent.getChildAdapterPosition(view)
        val column = position%spanCount
        val row = position / spanCount
        val lastRow = (maxCount?.minus(1))?.div(spanCount)

        outRect.left = column * spacing.div(spanCount)
        outRect.right = spacing.minus(column.plus(1) * spacing.div(spanCount))
        outRect.top = spacing * 2

        if(row == lastRow) {
            outRect.bottom = outerMargin
        }
    }
}