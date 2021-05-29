package com.codepan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout {

	private float previous;
	private int slop;

	public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.slop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				previous = MotionEvent.obtain(event).getX();
				break;
			case MotionEvent.ACTION_MOVE:
				final float x = event.getX();
				float difference = Math.abs(x - previous);
				if(difference > slop) {
					return false;
				}
		}
		return super.onInterceptTouchEvent(event);
	}
}
