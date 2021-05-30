package com.codepan.widget.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.R;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.calendar.model.YearData;

import java.util.ArrayList;

public class CalendarYearAdapter extends ArrayAdapter<YearData> {

	private final ArrayList<YearData> items;
	private final LayoutInflater inflater;
	private int textColor, textSize;
	private final int itemHeight;
	private String font;

	public CalendarYearAdapter(Context context, ArrayList<YearData> items, int itemHeight) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.itemHeight = itemHeight;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final YearData obj = items.get(position);
		if(obj != null) {
			if(convertView == null) {
				view = inflater.inflate(R.layout.calendar_year_item, parent, false);
				holder = new ViewHolder();
				holder.tvYear = view.findViewById(R.id.tvYear);
				holder.tvYear.setHeight(itemHeight);
				if (textColor != 0) {
					holder.tvYear.setTextColor(textColor);
				}
				if (textSize != 0) {
					holder.tvYear.setTextSize(textSize);
				}
				if (font != null) {
					holder.tvYear.setFont(font);
				}
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if (holder.tvYear != null) {
				holder.tvYear.setText(obj.name);
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
		private CodePanLabel tvYear;
	}
}
