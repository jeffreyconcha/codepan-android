package com.codepan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class AutoSquareFrameLayout extends FrameLayout {

	private boolean isAutoSquareDisabled;

	public AutoSquareFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(!isAutoSquareDisabled) {
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int height = MeasureSpec.getSize(heightMeasureSpec);
			if(height > width) {
				int specification = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
				super.onMeasure(specification, specification);
			}
			else {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		}
		else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setAutoSquareDisabled(boolean isAutoSquareDisabled) {
		this.isAutoSquareDisabled = isAutoSquareDisabled;
	}
}
