package com.codepan.widget.timepicker.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.codepan.R;
import com.codepan.callback.Interface.OnCancelCallback;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.timepicker.adapter.TimePickerAdapter;
import com.codepan.widget.timepicker.callback.Interface.OnPickTimeCallback;
import com.codepan.widget.timepicker.constant.TimeElementType;
import com.codepan.widget.timepicker.model.TimePickerData;

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

public class TimePickerView extends FrameLayout {
	private final String PATTERN_12HR = "hh:mm aa";
	private final String PATTERN_24HR = "HH:mm:ss";
	private int hourLastPosition, minuteLastPosition;

	private int titleTextSize, titlePadding, contentWidth, contentHeight,
		contentSize, contentSpacing, periodWidth, periodHeight, periodTextSize,
		buttonWidth, buttonHeight, buttonTextSize, buttonPadding, buttonSpacing,
		colorSize, colonSpacing, timeTextSize;
	private int defaultTextColor, accentColor, hourTextColor, hourBackgroundColor,
		minuteTextColor, minuteBackgroundColor, contentUnselectedColor,
		periodBorderColor;
	private String title, titleFont, buttonFont;

	private int periodUnselectedColor, initialHour, initialMinute;
	private CodePanLabel tvTitleTimePicker, tvAMTimePicker, tvPMTimePicker;
	private CodePanButton btnCancelTimePicker, btnConfirmTimePicker;
	private RecyclerView rvHourTimePicker, rvMinutesTimePicker;
	private View vHourTimePicker, vMinuteTimePicker;
	private OnPickTimeCallback pickTimeCallback;
	private FrameLayout flContentTimePicker;
	private OnCancelCallback cancelCallback;
	private LinearLayout llPeriodTimePicker;
	private TimePickerData hour, minute;
	private String period, defaultTime;
	private final Context context;

	public TimePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		Resources res = getResources();
		periodUnselectedColor = res.getColor(R.color.tp_period_unselected_color);
		period = context.getString(R.string.tp_am);
		if (defaultTime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_24HR, Locale.ENGLISH);
			try {
				Date date = sdf.parse(defaultTime);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					initialHour = cal.get(Calendar.HOUR_OF_DAY);
					initialMinute = cal.get(Calendar.MINUTE);
					if (initialHour >= 12) {
						period = res.getString(R.string.tp_pm);
					}
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		title = res.getString(R.string.tp_title);
		titleFont = res.getString(R.string.tp_title_font);
		titleTextSize = res.getDimensionPixelSize(R.dimen.tp_title_text_size);
		titlePadding = res.getDimensionPixelSize(R.dimen.tp_title_padding);
		accentColor = res.getColor(R.color.tp_accent_color);
		defaultTextColor = res.getColor(R.color.tp_default_text_color);
		hourTextColor = res.getColor(R.color.tp_hour_text_color);
		hourBackgroundColor = res.getColor(R.color.tp_hour_background_color);
		minuteTextColor = res.getColor(R.color.tp_minute_text_color);
		minuteBackgroundColor = res.getColor(R.color.tp_minute_background_color);
		timeTextSize = res.getDimensionPixelSize(R.dimen.tp_time_text_size);
		periodWidth = res.getDimensionPixelSize(R.dimen.tp_period_width);
		periodHeight = res.getDimensionPixelSize(R.dimen.tp_period_height);
		periodTextSize = res.getDimensionPixelSize(R.dimen.tp_period_text_size);
		buttonFont = res.getString(R.string.tp_button_font);
		buttonTextSize = res.getDimensionPixelSize(R.dimen.tp_button_text_size);
		buttonWidth = res.getDimensionPixelSize(R.dimen.tp_button_width);
		buttonHeight = res.getDimensionPixelSize(R.dimen.tp_button_height);
		buttonSpacing = res.getDimensionPixelSize(R.dimen.tp_button_spacing);
		buttonPadding = res.getDimensionPixelSize(R.dimen.tp_button_padding);
		setProperties(attrs);
	}

	private void setProperties(AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimePickerView);
		String _title = ta.getString(R.styleable.TimePickerView_title);
		String _titleFont = ta.getString(R.styleable.TimePickerView_titleFont);
		String _buttonFont = ta.getString(R.styleable.TimePickerView_buttonFont);
		title = _title != null ? _title : title;
		titleFont = _titleFont != null ? _titleFont : titleFont;
		titleTextSize = ta.getDimensionPixelSize(R.styleable.TimePickerView_titleTextSize, titleTextSize);
		titlePadding = ta.getDimensionPixelSize(R.styleable.TimePickerView_titlePadding, titlePadding);
		defaultTextColor = ta.getColor(R.styleable.TimePickerView_defaultTexColor, defaultTextColor);
		accentColor = ta.getColor(R.styleable.TimePickerView_accentColor, accentColor);
		defaultTextColor = ta.getColor(R.styleable.TimePickerView_defaultTexColor, defaultTextColor);
		hourTextColor = ta.getColor(R.styleable.TimePickerView_hourTextColor, hourTextColor);
		hourBackgroundColor = ta.getColor(R.styleable.TimePickerView_hourTextColor, hourBackgroundColor);
		minuteTextColor = ta.getColor(R.styleable.TimePickerView_minuteTextColor, minuteTextColor);
		minuteBackgroundColor = ta.getColor(R.styleable.TimePickerView_minuteTextColor, minuteBackgroundColor);
		timeTextSize = ta.getDimensionPixelSize(R.styleable.TimePickerView_timeTextSize, timeTextSize);
		periodWidth = ta.getDimensionPixelSize(R.styleable.TimePickerView_periodWidth, periodWidth);
		periodHeight = ta.getDimensionPixelSize(R.styleable.TimePickerView_periodHeight, periodHeight);
		periodTextSize = ta.getDimensionPixelSize(R.styleable.TimePickerView_periodTextSize, periodTextSize);
		buttonFont = _buttonFont != null ? _buttonFont : buttonFont;
		buttonTextSize = ta.getDimensionPixelSize(R.styleable.TimePickerView_buttonTextSize, buttonTextSize);
		buttonWidth = ta.getDimensionPixelSize(R.styleable.TimePickerView_buttonWidth, buttonWidth);
		buttonHeight = ta.getDimensionPixelSize(R.styleable.TimePickerView_buttonHeight, buttonHeight);
		buttonSpacing = ta.getDimensionPixelSize(R.styleable.TimePickerView_buttonSpacing, buttonSpacing);
		buttonPadding = ta.getDimensionPixelSize(R.styleable.TimePickerView_buttonPadding, buttonPadding);
		ta.recycle();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = inflate(context, R.layout.time_picker_layout, this);
		flContentTimePicker = view.findViewById(R.id.flContentTimePicker);
		llPeriodTimePicker = view.findViewById(R.id.llPeriodTimePicker);
		rvHourTimePicker = view.findViewById(R.id.rvHourTimePicker);
		rvMinutesTimePicker = view.findViewById(R.id.rvMinutesTimePicker);
		tvTitleTimePicker = view.findViewById(R.id.tvTitleTimePicker);
		tvAMTimePicker = view.findViewById(R.id.tvAMTimePicker);
		tvPMTimePicker = view.findViewById(R.id.tvPMTimePicker);
		btnCancelTimePicker = view.findViewById(R.id.btnCancelTimePicker);
		btnConfirmTimePicker = view.findViewById(R.id.btnConfirmTimePicker);
		vHourTimePicker = view.findViewById(R.id.vHourTimePicker);
		vMinuteTimePicker = view.findViewById(R.id.vMinuteTimePicker);
		loadItems(TimeElementType.HOUR, rvHourTimePicker);
		loadItems(TimeElementType.MINUTE, rvMinutesTimePicker);
		tvAMTimePicker.setOnClickListener(v -> {
			period = context.getString(R.string.tp_am);
			setDayPeriod(period);
		});
		tvPMTimePicker.setOnClickListener(v -> {
			period = context.getString(R.string.tp_pm);
			setDayPeriod(period);
		});
		btnCancelTimePicker.setOnClickListener(v -> {
			if (cancelCallback != null) {
				cancelCallback.onCancel();
			}
		});
		btnConfirmTimePicker.setOnClickListener(v -> {
			String time = hour.display + ":" + minute.display + " " + period;
			SimpleDateFormat input = new SimpleDateFormat(PATTERN_12HR, Locale.ENGLISH);
			SimpleDateFormat output = new SimpleDateFormat(PATTERN_24HR, Locale.ENGLISH);
			try {
				Date date = input.parse(time);
				if (date != null) {
					String military = output.format(date);
					if (pickTimeCallback != null) {
						pickTimeCallback.onPickTime(military);
					}
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		});
		setDayPeriod(period);
		applyProperties();
	}

	private void applyProperties() {
		tvTitleTimePicker.setText(title);
		tvTitleTimePicker.setTextSize(titleTextSize);
		tvTitleTimePicker.setPadding(titlePadding);
		tvTitleTimePicker.setTextColor(defaultTextColor);
		btnCancelTimePicker.setTextColor(defaultTextColor);
		btnConfirmTimePicker.setTextColor(accentColor);


		btnCancelTimePicker.setFont(buttonFont);
		btnCancelTimePicker.getLayoutParams().width = buttonWidth;
		btnCancelTimePicker.getLayoutParams().height = buttonHeight;
		btnCancelTimePicker.setPadding(buttonPadding);
		btnCancelTimePicker.setTextSize(buttonTextSize);
		btnConfirmTimePicker.setFont(buttonFont);
		btnConfirmTimePicker.getLayoutParams().width = buttonWidth;
		btnConfirmTimePicker.getLayoutParams().height = buttonHeight;
		btnConfirmTimePicker.setPadding(buttonPadding);
		btnConfirmTimePicker.setTextSize(buttonTextSize);


		vHourTimePicker.getLayoutParams().width = contentWidth;
		vHourTimePicker.getLayoutParams().height = contentHeight;
		vMinuteTimePicker.getLayoutParams().width = contentWidth;
		vMinuteTimePicker.getLayoutParams().height = contentHeight;
		llPeriodTimePicker.getLayoutParams().width = periodWidth;
		llPeriodTimePicker.getLayoutParams().height = Math.min(periodHeight, contentHeight);
		flContentTimePicker.getLayoutParams().width = (contentWidth * 2) + contentSpacing;
		flContentTimePicker.getLayoutParams().height = contentHeight * 3;


		findViewById(R.id.vDividerTimePicker).setBackgroundColor(accentColor);
		findViewById(R.id.vButtonTimePicker).getLayoutParams().width = buttonSpacing;
		GradientDrawable h = (GradientDrawable) vHourTimePicker.getBackground();
		h.setColor(hourBackgroundColor);
//		h.setAlpha(80);
		GradientDrawable m = (GradientDrawable) vMinuteTimePicker.getBackground();
		h.setColor(minuteBackgroundColor);
//		h.setAlpha(80);
	}

	private void setDayPeriod(String period) {
		if (period != null) {
			if (period.equals(context.getString(R.string.tp_am))) {
				tvAMTimePicker.setTextColor(accentColor);
				tvPMTimePicker.setTextColor(periodUnselectedColor);
				tvAMTimePicker.setBackgroundResource(R.drawable.tp_am_active_background);
				tvPMTimePicker.setBackgroundResource(R.drawable.tp_pm_inactive_background);
			}
			else {
				tvAMTimePicker.setTextColor(periodUnselectedColor);
				tvPMTimePicker.setTextColor(accentColor);
				tvAMTimePicker.setBackgroundResource(R.drawable.tp_am_inactive_background);
				tvPMTimePicker.setBackgroundResource(R.drawable.tp_pm_active_background);
			}
		}
	}

	private void loadItems(final TimeElementType type, final RecyclerView view) {
		int max = type == TimeElementType.HOUR ? 12 : 59;
		int min = type == TimeElementType.HOUR ? 1 : 0;
		final ArrayList<TimePickerData> itemList = new ArrayList<>();
		for (int i = min; i <= max; i++) {
			TimePickerData data = new TimePickerData();
			data.value = i;
			data.display = type == TimeElementType.HOUR ? String.valueOf(i) :
				String.format(Locale.ENGLISH, "%02d", i);
			itemList.add(data);
		}
		SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(view);
		final LinearLayoutManager manager = new LinearLayoutManager(context);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		view.setLayoutManager(manager);
		int selectedColor = type == TimeElementType.HOUR ? hourTextColor : minuteTextColor;
		final TimePickerAdapter adapter = new TimePickerAdapter(context,
			itemList, selectedColor, timeTextSize);
		adapter.setOnItemClickCallback((position, v, parent) -> {
			switch (type) {
				case HOUR:
					if (position < hourLastPosition) {
						view.smoothScrollToPosition(position - 1);
					}
					else if (position > hourLastPosition) {
						view.smoothScrollToPosition(position + 1);
					}
					break;
				case MINUTE:
					if (position < minuteLastPosition) {
						view.smoothScrollToPosition(position - 1);
					}
					else if (position > minuteLastPosition) {
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

	public void setOnCancelCallback(OnCancelCallback cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

	public void setDefaultTime(String defaultTime) {
		this.defaultTime = defaultTime;
	}
}
