package com.codepan.widget.calendar.view;


import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.widget.calendar.adapter.CalendarDayAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.widget.calendar.callback.Interface.OnSelectDateCallback;
import com.codepan.widget.calendar.model.DayData;

import java.util.ArrayList;

public class CalendarDay extends FrameLayout {

	private OnSelectDateCallback selectDateCallback;
	private OnPickDateCallback pickDateCallback;
	private CalendarDayAdapter adapter;
	private ArrayList<DayData> dayList;
	private GridView gvCalendarDay;
	private final int numRows;
	private final int spacing;

	public CalendarDay(Context context) {
		super(context);
		Resources res = getResources();
		this.numRows = res.getInteger(R.integer.day_row);
		this.spacing = res.getDimensionPixelSize(R.dimen.cal_spacing);
	}

	public void init(ArrayList<DayData> dayList, OnPickDateCallback pickDateCallback,
					 OnSelectDateCallback selectDateCallback) {
		this.dayList = dayList;
		this.pickDateCallback = pickDateCallback;
		this.selectDateCallback = selectDateCallback;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = inflate(getContext(), R.layout.calendar_day_layout, this);
		gvCalendarDay = view.findViewById(R.id.gvCalendarDay);
		gvCalendarDay.setOnItemClickListener((arg0, v, position, arg3) -> {
			int index = getLastSelected();
			dayList.get(index).isSelect = false;
			dayList.get(position).isSelect = true;
			adapter.notifyDataSetChanged();
			gvCalendarDay.invalidate();
			if (pickDateCallback != null) {
				pickDateCallback.onPickDate(dayList.get(position).date);
			}
			if (selectDateCallback != null) {
				selectDateCallback.onSelectDate(dayList.get(position).date);
			}
		});
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (adapter == null) {
			int itemHeight = (getHeight() / numRows) - spacing;
			adapter = new CalendarDayAdapter(getContext(), dayList, itemHeight);
			gvCalendarDay.setAdapter(adapter);
		}
	}

	public int getLastSelected() {
		for (DayData day : dayList) {
			if (day.isSelect) {
				return dayList.indexOf(day);
			}
		}
		return 0;
	}

	public void setSelected(String date) {
		for (DayData day : dayList) {
			day.isSelect = day.date.equals(date);
		}
		adapter.notifyDataSetChanged();
		gvCalendarDay.invalidate();
	}
}
