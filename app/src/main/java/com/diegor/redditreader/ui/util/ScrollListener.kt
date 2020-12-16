package com.diegor.redditreader.ui.util

class ScrollListener(
    private val listener: InfiniteScrollListener,
    private val layoutManager: androidx.recyclerview.widget.LinearLayoutManager
) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true

    private var visibleThreshold = 6
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > 0) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }

            if (totalItemCount - visibleItemCount <= (firstVisibleItem + visibleThreshold)) {
                listener.onReachedBottom()
                loading = true
            }
        }
    }
}