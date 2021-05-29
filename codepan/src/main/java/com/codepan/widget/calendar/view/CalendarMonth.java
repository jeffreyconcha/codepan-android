package com.codepan.widget.calendar.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.app.CPFragment;
import com.codepan.widget.calendar.adapter.CalendarMonthAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickMonthCallback;
import com.codepan.widget.calendar.model.MonthData;

import java.util.ArrayList;

public class CalendarMonth extends CPFragment {

	private OnPickMonthCallback pickMonthCallback;
	private ArrayList<MonthData> monthList;
	private CalendarMonthAdapter adapter;
	private GridView gvCalendarMonth;
	private int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.disableBackPressed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_month_layout, container, false);
		gvCalendarMonth = view.findViewById(R.id.gvCalendarMonth);
		gvCalendarMonth.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(pickMonthCallback != null) {
					pickMonthCallback.onPickMonth(monthList.get(i));
				}
			}
		});
		adapter = new CalendarMonthAdapter(activity, monthList, height);
		gvCalendarMonth.setAdapter(adapter);
		return view;
	}

	public void init(ArrayList<MonthData> monthList, int height, OnPickMonthCallback pickMonthCallback) {
		this.monthList = monthList;
		this.pickMonthCallback = pickMonthCallback;
		this.height = height;
	}
}
