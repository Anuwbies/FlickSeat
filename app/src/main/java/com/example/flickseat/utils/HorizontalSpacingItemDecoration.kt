package com.example.flickseat.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position != RecyclerView.NO_POSITION) {
            outRect.left = if (position == 0) 0 else space / 2
            outRect.right = if (position == itemCount - 1) 0 else space / 2
        }
    }
}
