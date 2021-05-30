package com.codepan.widget.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.R;
import com.codepan.widget.calendar.model.MonthData;
import com.codepan.widget.CodePanLabel;

import java.util.ArrayList;

public class CalendarMonthAdapter extends ArrayAdapter<MonthData> {

	private ArrayList<MonthData> items;
	private LayoutInflater inflater;
	private int itemHeight;

	public CalendarMonthAdapter(Context context, ArrayList<MonthData> items, int itemHeight) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.itemHeight = itemHeight;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final MonthData obj = items.get(position);
		if(obj != null) {
			if(convertView == null) {
				view = inflater.inflate(R.layout.calendar_month_item, parent, false);
				holder = new ViewHolder();
				holder.tvMonth = view.findViewById(R.id.tvMonth);
				holder.tvMonth.setHeight(itemHeight);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(holder.tvMonth != null) {
				holder.tvMonth.setText(obj.name);
			}
		}
		return view;
	}

	private static class ViewHolder {
		private CodePanLabel tvMonth;
	}
}
