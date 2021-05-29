package com.codepan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

public class LockableNestedScrollView extends NestedScrollView {

	private boolean isScrollable = true;

	public LockableNestedScrollView(@NonNull Context context) {
		super(context);
	}

	public LockableNestedScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollingEnabled(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch(ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return isScrollable && super.onTouchEvent(ev);
			default:
				return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(!isScrollable) return false;
		else return super.onInterceptTouchEvent(ev);
	}

	public boolean isScrollable() {
		return isScrollable;
	}
}
