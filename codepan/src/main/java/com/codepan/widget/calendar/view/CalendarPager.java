package com.codepan.widget.calendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.codepan.R;
import com.codepan.widget.calendar.adapter.CalendarDayAdapter;

import androidx.viewpager.widget.ViewPager;

public class CalendarPager extends ViewPager {

	private boolean isSet;

	public CalendarPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(!isSet) {
			View child = getChildAt(0);
			if(child instanceof GridView) {
				GridView gvCalendarDay = (GridView) child;
				ListAdapter adapter = gvCalendarDay.getAdapter();
				if(adapter instanceof CalendarDayAdapter) {
					CalendarDayAdapter day = (CalendarDayAdapter) adapter;
					final ViewGroup parent = day.getParent();
					int mWidth = parent.getMeasuredWidth();
					int mHeight = parent.getMeasuredHeight();
					final Resources res = getResources();
					int numCol = res.getInteger(R.integer.day_col);
					int numRow = res.getInteger(R.integer.day_row);
					int spacing = res.getDimensionPixelSize(R.dimen.cal_spacing);
					int width = (mWidth + spacing) * numCol;
					//getLayoutParams().width = width;
					getLayoutParams().height = (mHeight + spacing) * numRow;
				}
			}
			isSet = true;
		}
	}

	public void reset() {
		this.isSet = false;
	}
}
