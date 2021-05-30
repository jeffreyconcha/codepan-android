package com.codepan.widget.calendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.widget.calendar.adapter.CalendarYearAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickYearCallback;
import com.codepan.widget.calendar.model.YearData;

import java.util.ArrayList;

public class CalendarYear extends FrameLayout {

	private OnPickYearCallback pickYearCallback;
	private ArrayList<YearData> yearList;
	private CalendarYearAdapter adapter;
	private GridView gvCalendarYear;
	private final int numRows;
	private final int spacing;

	public CalendarYear(Context context) {
		super(context);
		Resources res = getResources();
		this.numRows = res.getInteger(R.integer.year_row);
		this.spacing = res.getDimensionPixelSize(R.dimen.cal_spacing);
	}

	public void init(ArrayList<YearData> yearList, OnPickYearCallback pickYearCallback) {
		this.yearList = yearList;
		this.pickYearCallback = pickYearCallback;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = inflate(getContext(), R.layout.calendar_year_layout, this);
		gvCalendarYear = view.findViewById(R.id.gvCalendarYear);
		gvCalendarYear.setOnItemClickListener((adapterView, view1, i, l) -> {
			if (pickYearCallback != null) {
				pickYearCallback.onPickYear(yearList.get(i));
			}
		});
		ViewGroup parent = (ViewGroup) getParent();
		int itemHeight = parent.getHeight() / numRows;
		adapter = new CalendarYearAdapter(getContext(), yearList, itemHeight);
		gvCalendarYear.setAdapter(adapter);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (adapter == null) {
			int itemHeight = (getHeight() / numRows) - spacing;
			adapter = new CalendarYearAdapter(getContext(), yearList, itemHeight);
			gvCalendarYear.setAdapter(adapter);
		}
	}
}
