package com.codepan.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.codepan.callback.Interface.OnScrollChangeCallback;


public class TableScrollView extends ScrollView {

	private OnScrollChangeCallback scrollChangeCallback;

	public TableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int ol, int ot) {
		if(scrollChangeCallback != null) {
			scrollChangeCallback.onScrollChanged(l, t, ol, ot);
		}
	}

	public void setOnScrollChangeCallback(OnScrollChangeCallback scrollChangeCallback) {
		this.scrollChangeCallback = scrollChangeCallback;
	}
}