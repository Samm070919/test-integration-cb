package com.pagatodoholdings.posandroid.secciones.calendar.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(spanCount: Int, spacing: Int, includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    private var spanCount = spanCount
    private var spacing = spacing
    private var includeEdge = includeEdge

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        var position: Int = parent.getChildAdapterPosition(view)
        var column: Int = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }

}