package com.codepan.widget.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.R;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.calendar.model.MonthData;

import java.util.ArrayList;

public class CalendarMonthAdapter extends ArrayAdapter<MonthData> {

	private final ArrayList<MonthData> items;
	private final LayoutInflater inflater;
	private int textColor, textSize;
	private final int itemHeight;
	private String font;

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
				if (textColor != 0) {
					holder.tvMonth.setTextColor(textColor);
				}
				if (textSize != 0) {
					holder.tvMonth.setTextSize(textSize);
				}
				if (font != null) {
					holder.tvMonth.setFont(font);
				}
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if (holder.tvMonth != null) {
				holder.tvMonth.setText(obj.name);
			}
		}
		return view;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public void setFont(String font) {
		this.font = font;
	}

	private static class ViewHolder {
		private CodePanLabel tvMonth;
	}
}
