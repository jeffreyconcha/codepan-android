package com.codepan.widget.timepicker.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.R;
import com.codepan.callback.Interface.OnItemClickCallback;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.timepicker.model.TimePickerData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimePickerAdapter extends RecyclerView.Adapter<TimePickerAdapter.ViewHolder> {

	private final int selectedColor, defaultColor;
	private OnItemClickCallback itemClickCallback;
	private final ArrayList<TimePickerData> items;
	private final LayoutInflater inflater;
	private final int textSize;

	public TimePickerAdapter(Context context, ArrayList<TimePickerData> items,
							 int selectedColor, int textSize) {
		this.inflater = LayoutInflater.from(context);
		this.items = items;
		this.selectedColor = selectedColor;
		this.textSize = textSize;
		Resources res = context.getResources();
		this.defaultColor = res.getColor(R.color.tp_content_unselected_text_color);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.time_picker_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		int index = getIndex(position);
		TimePickerData data = items.get(index);
		if(data != null) {
			holder.tvValue.setTextSize(textSize);
			holder.tvValue.setText(data.display);
			if(data.isSelected) {
				holder.tvValue.setTextColor(selectedColor);
			}
			else {
				holder.tvValue.setTextColor(defaultColor);
			}
			holder.tvValue.setOnClickListener(v -> {
				if(itemClickCallback != null) {
					ViewGroup parent = (ViewGroup) v.getParent();
					itemClickCallback.onItemClick(position, v, parent);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}

	public int getIndex(int position) {
		return position % items.size();
	}

	public void setOnItemClickCallback(OnItemClickCallback itemClickCallback) {
		this.itemClickCallback = itemClickCallback;
	}

	public void setHourTextColor(int hourTextColor) {
	}

	public void setMinuteTextColor(int minuteTextColor) {
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		CodePanLabel tvValue;

		ViewHolder(View view) {
			super(view);
			tvValue = (CodePanLabel) view;
		}
	}
}
