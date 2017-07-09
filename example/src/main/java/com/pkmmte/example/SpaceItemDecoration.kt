package com.pkmmte.example

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.LinearLayoutManager.VERTICAL

/**
 * Created on March 10, 2017

 * @author Pkmmte Xeleon
 */
class SpaceItemDecoration(val verticalSize: Int = 0, val horizontalSize: Int = 0, val orientation: Int = VERTICAL) : RecyclerView.ItemDecoration() {
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
		val position = parent.getChildAdapterPosition(view)

		if (orientation == VERTICAL) {
			if (position != 0) {
				outRect.top = verticalSize / 2
			}
			if (position != parent.adapter.itemCount - 1) {
				outRect.bottom = verticalSize / 2
			}

			outRect.left = horizontalSize
			outRect.right = horizontalSize
		} else if (orientation == HORIZONTAL) {
			if (position != 0) {
				outRect.left = horizontalSize / 2
			}
			if (position != parent.adapter.itemCount - 1) {
				outRect.right = horizontalSize / 2
			}

			outRect.top = verticalSize
			outRect.bottom = verticalSize
		}
	}
}
