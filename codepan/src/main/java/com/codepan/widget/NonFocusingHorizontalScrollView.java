package com.codepan.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class NonFocusingHorizontalScrollView extends HorizontalScrollView {

	public NonFocusingHorizontalScrollView(Context context) {
		super(context);
	}

	public NonFocusingHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonFocusingHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		return true;
	}
}
