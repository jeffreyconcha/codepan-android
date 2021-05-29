package com.codepan.widget.calendar.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.app.CPFragment;
import com.codepan.widget.calendar.adapter.CalendarDayAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.widget.calendar.callback.Interface.OnSelectDateCallback;
import com.codepan.widget.calendar.model.DayData;

import java.util.ArrayList;

public class CalendarDay extends CPFragment {

	private OnSelectDateCallback selectDateCallback;
	private OnPickDateCallback pickDateCallback;
	private CalendarDayAdapter adapter;
	private ArrayList<DayData> dayList;
	private GridView gvCalendarDay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.disableBackPressed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_day_layout, container, false);
		gvCalendarDay = view.findViewById(R.id.gvCalendarDay);
		gvCalendarDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				int index = getLastSelected();
				dayList.get(index).isSelect = false;
				dayList.get(position).isSelect = true;
				adapter.notifyDataSetChanged();
				gvCalendarDay.invalidate();
				if(pickDateCallback != null) {
					pickDateCallback.onPickDate(dayList.get(position).date);
				}
				if(selectDateCallback != null) {
					selectDateCallback.onSelectDate(dayList.get(position).date);
				}
			}
		});
		adapter = new CalendarDayAdapter(activity, dayList);
		gvCalendarDay.setAdapter(adapter);
		return view;
	}

	public void init(ArrayList<DayData> dayList, OnPickDateCallback pickDateCallback,
			OnSelectDateCallback selectDateCallback) {
		this.dayList = dayList;
		this.pickDateCallback = pickDateCallback;
		this.selectDateCallback = selectDateCallback;
	}

	public int getLastSelected() {
		for(DayData obj : dayList) {
			if(obj.isSelect) {
				return dayList.indexOf(obj);
			}
		}
		return 0;
	}

	public void setSelected(String date) {
		for(DayData obj : dayList) {
			obj.isSelect = obj.date.equals(date);
		}
		adapter.notifyDataSetChanged();
		gvCalendarDay.invalidate();
	}
}
