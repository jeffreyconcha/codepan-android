package com.codepan.widget.timepicker.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.R;
import com.codepan.callback.Interface.OnItemClickCallback;
import com.codepan.widget.timepicker.constant.TimeElementType;
import com.codepan.widget.timepicker.model.TimePickerData;
import com.codepan.widget.CodePanLabel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class TimePickerAdapter extends RecyclerView.Adapter<TimePickerAdapter.ViewHolder> {

	private OnItemClickCallback itemClickCallback;
	private ArrayList<TimePickerData> items;
	private int defaultColor, selectedColor;
	private LayoutInflater inflater;

	public TimePickerAdapter(Context context, ArrayList<TimePickerData> items, TimeElementType type) {
		this.inflater = LayoutInflater.from(context);
		this.items = items;
		Resources res = context.getResources();
		switch(type) {
			case HOUR:
				this.selectedColor = res.getColor(R.color.tp_hour);
				break;
			case MINUTE:
				this.selectedColor = res.getColor(R.color.tp_minute);
				break;
		}
		this.defaultColor = res.getColor(R.color.tp_not_selected_1);
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
			holder.tvValue.setText(data.display);
			if(data.isSelected) {
				holder.tvValue.setTextColor(selectedColor);
			}
			else {
				holder.tvValue.setTextColor(defaultColor);
			}
			holder.tvValue.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(itemClickCallback != null) {
						ViewGroup parent = (ViewGroup) v.getParent();
						itemClickCallback.onItemClick(position, v, parent);
					}
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

	class ViewHolder extends RecyclerView.ViewHolder {

		CodePanLabel tvValue;

		ViewHolder(View view) {
			super(view);
			tvValue = (CodePanLabel) view;
		}
	}
}
