package com.codepan.widget.calendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.widget.calendar.adapter.CalendarMonthAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickMonthCallback;
import com.codepan.widget.calendar.model.MonthData;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CalendarMonth extends FrameLayout {

	private OnPickMonthCallback pickMonthCallback;
	private ArrayList<MonthData> monthList;
	private CalendarMonthAdapter adapter;
	private GridView gvCalendarMonth;
	private final int numRows;
	private final int spacing;

	public CalendarMonth(@NonNull Context context) {
		super(context);
		Resources res = getResources();
		this.numRows = res.getInteger(R.integer.month_row);
		this.spacing = res.getDimensionPixelSize(R.dimen.cal_spacing);
	}

	public void init(ArrayList<MonthData> monthList, OnPickMonthCallback pickMonthCallback) {
		this.monthList = monthList;
		this.pickMonthCallback = pickMonthCallback;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = inflate(getContext(), R.layout.calendar_month_layout, this);
		gvCalendarMonth = view.findViewById(R.id.gvCalendarMonth);
		gvCalendarMonth.setOnItemClickListener((adapterView, view1, i, l) -> {
			if (pickMonthCallback != null) {
				pickMonthCallback.onPickMonth(monthList.get(i));
			}
		});
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (adapter == null) {
			int itemHeight = (getHeight() / numRows) - spacing;
			adapter = new CalendarMonthAdapter(getContext(), monthList, itemHeight);
			gvCalendarMonth.setAdapter(adapter);
		}
	}
}
