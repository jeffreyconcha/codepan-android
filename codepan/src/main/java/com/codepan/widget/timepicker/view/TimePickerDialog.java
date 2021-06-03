package com.codepan.widget.timepicker.view;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.R;
import com.codepan.app.CPFragment;
import com.codepan.callback.Interface.OnItemClickCallback;
import com.codepan.widget.timepicker.adapter.TimePickerAdapter;
import com.codepan.widget.timepicker.callback.Interface.OnPickTimeCallback;
import com.codepan.widget.timepicker.constant.TimeElementType;
import com.codepan.widget.timepicker.model.TimePickerData;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SnapHelper;

public class TimePickerDialog extends CPFragment {
	private final String PATTERN_12HR = "hh:mm aa";
	private final String PATTERN_24HR = "HH:mm:ss";
	private int hourLastPosition, minuteLastPosition;

	private int selectedColor, unselectedColor, initialHour, initialMinute;
	private CodePanButton btnCancelTimePicker, btnSaveTimePicker;
	private RecyclerView rvHourTimePicker, rvMinutesTimePicker;
	private CodePanLabel tvAMTimePicker, tvPMTimePicker;
	private OnPickTimeCallback pickTimeCallback;
	private TimePickerData hour, minute;
	private String period, defaultTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = activity.getResources();
		selectedColor = res.getColor(R.color.tp_primary);
		unselectedColor = res.getColor(R.color.tp_not_selected_2);
		period = getString(R.string.tp_am);
		if(defaultTime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_24HR, Locale.ENGLISH);
			try {
				Date date = sdf.parse(defaultTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				initialHour = cal.get(Calendar.HOUR_OF_DAY);
				initialMinute = cal.get(Calendar.MINUTE);
				if(initialHour >= 12) {
					period = getString(R.string.tp_pm);
				}
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.time_picker_dialog_layout, container, false);
		rvHourTimePicker = view.findViewById(R.id.rvHourTimePicker);
		rvMinutesTimePicker = view.findViewById(R.id.rvMinutesTimePicker);
		tvAMTimePicker = view.findViewById(R.id.tvAMTimePicker);
		tvPMTimePicker = view.findViewById(R.id.tvPMTimePicker);
		btnCancelTimePicker = view.findViewById(R.id.btnCancelTimePicker);
		btnSaveTimePicker = view.findViewById(R.id.btnSaveTimePicker);
		loadItems(TimeElementType.HOUR, rvHourTimePicker);
		loadItems(TimeElementType.MINUTE, rvMinutesTimePicker);
		tvAMTimePicker.setOnClickListener(v -> {
			period = getString(R.string.tp_am);
			setDayPeriod(period);
		});
		tvPMTimePicker.setOnClickListener(v -> {
			period = getString(R.string.tp_pm);
			setDayPeriod(period);
		});
		btnCancelTimePicker.setOnClickListener(v -> onBackPressed());
		btnSaveTimePicker.setOnClickListener(v -> {
			manager.popBackStack();
			String time = hour.display + ":" + minute.display + " " + period;
			SimpleDateFormat input = new SimpleDateFormat(PATTERN_12HR, Locale.ENGLISH);
			SimpleDateFormat output = new SimpleDateFormat(PATTERN_24HR, Locale.ENGLISH);
			try {
				Date date = input.parse(time);
				String military = output.format(date);
				if(pickTimeCallback != null) {
					pickTimeCallback.onPickTime(military);
				}
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		});
		setDayPeriod(period);
		return view;
	}

	private void setDayPeriod(String period) {
		if(period != null) {
			if(period.equals(getString(R.string.tp_am))) {
				tvAMTimePicker.setTextColor(selectedColor);
				tvPMTimePicker.setTextColor(unselectedColor);
				tvAMTimePicker.setBackgroundResource(R.drawable.tp_am_active_background);
				tvPMTimePicker.setBackgroundResource(R.drawable.tp_pm_inactive_background);
			}
			else {
				tvAMTimePicker.setTextColor(unselectedColor);
				tvPMTimePicker.setTextColor(selectedColor);
				tvAMTimePicker.setBackgroundResource(R.drawable.tp_am_inactive_background);
				tvPMTimePicker.setBackgroundResource(R.drawable.tp_pm_active_background);
			}
		}
	}

	private void loadItems(final TimeElementType type, final RecyclerView view) {
		int max = type == TimeElementType.HOUR ? 12 : 59;
		int min = type == TimeElementType.HOUR ? 1 : 0;
		final ArrayList<TimePickerData> itemList = new ArrayList<>();
		for(int i = min; i <= max; i++) {
			TimePickerData data = new TimePickerData();
			data.value = i;
			data.display = type == TimeElementType.HOUR ? String.valueOf(i) :
					String.format(Locale.ENGLISH, "%02d", i);
			itemList.add(data);
		}
		SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(view);
		final LinearLayoutManager manager = new LinearLayoutManager(activity);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		view.setLayoutManager(manager);
		final TimePickerAdapter adapter = new TimePickerAdapter(activity, itemList, type);
		adapter.setOnItemClickCallback((position, v, parent) -> {
			switch(type) {
				case HOUR:
					if(position < hourLastPosition) {
						view.smoothScrollToPosition(position - 1);
					}
					else if(position > hourLastPosition) {
						view.smoothScrollToPosition(position + 1);
					}
					break;
				case MINUTE:
					if(position < minuteLastPosition) {
						view.smoothScrollToPosition(position - 1);
					}
					else if(position > minuteLastPosition) {
						view.smoothScrollToPosition(position + 1);
					}
					break;
			}
		});
		view.setAdapter(adapter);
		switch(type) {
			case HOUR: {
				int initial = (Integer.MAX_VALUE / 2) - 5 + initialHour;
				view.scrollToPosition(initial);
				int position = initial + 1;
				int index = adapter.getIndex(position);
				TimePickerData data = itemList.get(index);
				data.isSelected = true;
				adapter.notifyItemChanged(position);
				hourLastPosition = position;
				hour = data;
			}
			break;
			case MINUTE: {
				int initial = (Integer.MAX_VALUE / 2) - 4 + initialMinute;
				view.scrollToPosition(initial);
				int position = initial + 1;
				int index = adapter.getIndex(position);
				TimePickerData data = itemList.get(index);
				data.isSelected = true;
				adapter.notifyItemChanged(position);
				minuteLastPosition = position;
				minute = data;
			}
			break;
		}
		view.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView view, int newState) {
				super.onScrollStateChanged(view, newState);
				if(newState == RecyclerView.SCROLL_STATE_IDLE) {
					int position = manager.findFirstVisibleItemPosition() + 1;
					int index = adapter.getIndex(position);
					TimePickerData data = itemList.get(index);
					data.isSelected = true;
					adapter.notifyItemChanged(position);
					switch(type) {
						case HOUR: {
							hourLastPosition = position;
							hour = data;
						}
						break;
						case MINUTE: {
							minuteLastPosition = position;
							minute = data;
						}
						break;
					}
				}
				else {
					switch(type) {
						case HOUR: {
							int lastIndex = adapter.getIndex(hourLastPosition);
							TimePickerData time = itemList.get(lastIndex);
							time.isSelected = false;
							adapter.notifyItemChanged(hourLastPosition);
						}
						break;
						case MINUTE: {
							int lastIndex = adapter.getIndex(minuteLastPosition);
							TimePickerData time = itemList.get(lastIndex);
							time.isSelected = false;
							adapter.notifyItemChanged(minuteLastPosition);
						}
						break;
					}
				}
			}
		});
	}

	public void setOnPickTimeCallback(OnPickTimeCallback pickTimeCallback) {
		this.pickTimeCallback = pickTimeCallback;
	}

	public void setDefaultTime(String defaultTime) {
		this.defaultTime = defaultTime;
	}
}
