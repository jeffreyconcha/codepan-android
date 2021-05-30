package com.codepan.widget.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.R;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.calendar.model.DayData;

import java.util.ArrayList;

public class CalendarDayAdapter extends ArrayAdapter<DayData> {

	private int inActive, active, selected;
	private ArrayList<DayData> items;
	private LayoutInflater inflater;
	private int itemHeight;

	public CalendarDayAdapter(Context context, ArrayList<DayData> items, int itemHeight) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.inActive = context.getResources().getColor(R.color.cal_day_inactive);
		this.active = context.getResources().getColor(R.color.cal_day_active);
		this.selected = context.getResources().getColor(R.color.cal_day_selected);
		this.items = items;
		this.itemHeight = itemHeight;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final DayData day = items.get(position);
		if (day != null) {
			if (convertView == null) {
				view = inflater.inflate(R.layout.calendar_day_item, parent, false);
				holder = new ViewHolder();
				holder.tvDay = view.findViewById(R.id.tvDay);
				view.getLayoutParams().height = itemHeight;
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if (holder.tvDay != null) {
				holder.tvDay.setText(String.valueOf(day.id));
				if (day.isSelect) {
					holder.tvDay.setBackgroundResource(R.drawable.state_oval_cal_selected);
					holder.tvDay.setTextColor(selected);
				}
				else {
					holder.tvDay.setBackgroundResource(R.drawable.state_oval_trans_dark);
					if (day.isActive) {
						holder.tvDay.setTextColor(active);
					}
					else {
						holder.tvDay.setTextColor(inActive);
					}
				}
			}
		}
		return view;
	}

	private static class ViewHolder {
		private CodePanLabel tvDay;
	}
}
