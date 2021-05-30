package com.codepan.widget.calendar.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.R;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.calendar.model.DayData;

import java.util.ArrayList;

public class CalendarDayAdapter extends ArrayAdapter<DayData> {

	private final int inActive, active, selected, itemHeight;
	private int textColor, textSize, selectedColor;
	private final ArrayList<DayData> items;
	private final LayoutInflater inflater;
	private String font;

	public CalendarDayAdapter(Context context, ArrayList<DayData> items, int itemHeight) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Resources res = context.getResources();
		this.inActive = res.getColor(R.color.cal_inactive_text_color);
		this.active = res.getColor(R.color.cal_day_active);
		this.selected = res.getColor(R.color.cal_day_selected);
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
				if (textColor != 0) {
					holder.tvDay.setTextColor(textColor);
				}
				if (textSize != 0) {
					holder.tvDay.setTextSize(textSize);
				}
				if (font != null) {
					holder.tvDay.setFont(font);
				}
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
					GradientDrawable drawable = (GradientDrawable) holder.tvDay.getBackground();
					drawable.setColor(selectedColor);
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

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}

	private static class ViewHolder {
		private CodePanLabel tvDay;
	}
}
