package com.codepan.widget.listview

import android.content.Context
import android.util.AttributeSet
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import android.widget.ListView

interface ScrollNotifier {
    fun onTouchScroll();
    fun onScrollToMax();
}

class ExpandableListView(context: Context, attrs: AttributeSet) :
    ListView(context, attrs), AbsListView.OnScrollListener {

    private var notifier: ScrollNotifier? = null
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    init {
        setOnScrollListener(this)
    }

    override fun onScrollStateChanged(view: AbsListView?, state: Int) {
        when (state) {
            SCROLL_STATE_TOUCH_SCROLL -> {
                notifier?.onTouchScroll()
            }
            SCROLL_STATE_IDLE -> {
                if (firstVisibleItem == totalItemCount - visibleItemCount) {
                    notifier?.onScrollToMax()
                }
            }
        }
    }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        this.firstVisibleItem = firstVisibleItem
        this.visibleItemCount = visibleItemCount
        this.totalItemCount = totalItemCount
    }

    fun setScrollNotifier(notifier: ScrollNotifier) {
        this.notifier = notifier
    }
}