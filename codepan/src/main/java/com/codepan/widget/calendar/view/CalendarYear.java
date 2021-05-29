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
import com.codepan.widget.calendar.adapter.CalendarYearAdapter;
import com.codepan.widget.calendar.callback.Interface.OnPickYearCallback;
import com.codepan.widget.calendar.model.YearData;

import java.util.ArrayList;

public class CalendarYear extends CPFragment {

	private OnPickYearCallback pickYearCallback;
	private ArrayList<YearData> yearList;
	private CalendarYearAdapter adapter;
	private GridView gvCalendarYear;
	private int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.disableBackPressed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_year_layout, container, false);
		gvCalendarYear = view.findViewById(R.id.gvCalendarYear);
		gvCalendarYear.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(pickYearCallback != null) {
					pickYearCallback.onPickYear(yearList.get(i));
				}
			}
		});
		adapter = new CalendarYearAdapter(activity, yearList, height);
		gvCalendarYear.setAdapter(adapter);
		return view;
	}

	public void init(ArrayList<YearData> yearList, int height, OnPickYearCallback pickYearCallback) {
		this.yearList = yearList;
		this.pickYearCallback = pickYearCallback;
		this.height = height;
	}
}
