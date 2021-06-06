package com.codepan.widget.calendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.codepan.R;
import com.codepan.adapter.ViewPagerAdapter;
import com.codepan.callback.Interface.OnCancelCallback;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.calendar.callback.Interface.OnPickMonthCallback;
import com.codepan.widget.calendar.callback.Interface.OnPickYearCallback;
import com.codepan.widget.calendar.callback.Interface.OnSelectDateCallback;
import com.codepan.widget.calendar.model.DayData;
import com.codepan.widget.calendar.model.MonthData;
import com.codepan.widget.calendar.model.YearData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import static com.codepan.widget.calendar.callback.Interface.OnPickDateCallback;

public class CalendarView extends FrameLayout implements OnPickDateCallback, OnSelectDateCallback,
		OnPickMonthCallback, OnPickYearCallback {

	private final int PREVIOUS = 0;
	private final int CURRENT = 1;
	private final int NEXT = 2;
	private final int DAY_MODE = 0;
	private final int MONTH_MODE = 1;
	private final int YEAR_MODE = 2;
	private int lastPosition = CURRENT;
	private int mode = DAY_MODE;

	private int contentTextSize, contentPadding, titleTextSize, titlePadding, dateTextSize,
		yearTextSize, monthTextSize, arrowSize, arrowIconWidth, arrowIconHeight,
		buttonPadding, buttonTextSize, buttonWidth, buttonHeight, buttonSpacing;
	private int accentColor, defaultTextColor, selectedColor;
	private String title, titleFont, contentFont, buttonFont;

	private CodePanButton btnCancelCalendar, btnConfirmCalendar, btnMonthYearCalendar,
		btnPreviousCalendar, btnNextCalendar;
	private CodePanLabel tvTitleCalendar, tvYearCalendar, tvDateCalendar;
	private CalendarMonth prevCalMonth, currCalMonth, nextCalMonth;
	private ArrayList<View> dayCalList, yearCalList, monthCalList;
	private CalendarYear prevCalYear, currCalYear, nextCalYear;
	private CalendarDay prevCalDay, currCalDay, nextCalDay;
	private FrameLayout flNextCalendar, flPreviousCalendar;
	private View vNextCalendar, vPreviousCalendar;
	private OnPickDateCallback pickDateCallback;
	private OnCancelCallback cancelCallback;
	private LinearLayout llDayCalendar;
	private String date, selectedDate;
	private ViewPagerAdapter adapter;
	private ViewPager vpCalendar;
	private final Context context;
	private final int yearRow;
	private final int yearCol;
	private Calendar cal;

	public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		Resources res = getResources();
		yearRow = res.getInteger(R.integer.year_row);
		yearCol = res.getInteger(R.integer.year_col);
		title = res.getString(R.string.cal_title);
		titleFont = res.getString(R.string.cal_title_font);
		titlePadding = res.getDimensionPixelSize(R.dimen.cal_title_padding);
		titleTextSize = res.getDimensionPixelSize(R.dimen.cal_content_text_size);
		contentFont = res.getString(R.string.cal_content_font);
		defaultTextColor = res.getColor(R.color.cal_default_text_color);
		contentPadding = res.getDimensionPixelSize(R.dimen.cal_content_padding);
		contentTextSize = res.getDimensionPixelSize(R.dimen.cal_content_text_size);
		accentColor = res.getColor(R.color.cal_accent_color);
		selectedColor = res.getColor(R.color.cal_selected_color);
		dateTextSize = res.getDimensionPixelSize(R.dimen.cal_date_text_size);
		yearTextSize = res.getDimensionPixelSize(R.dimen.cal_year_text_size);
		monthTextSize = res.getDimensionPixelSize(R.dimen.cal_month_text_size);
		arrowSize = res.getDimensionPixelSize(R.dimen.cal_arrow_size);
		arrowIconWidth = res.getDimensionPixelSize(R.dimen.cal_arrow_icon_width);
		arrowIconHeight = res.getDimensionPixelSize(R.dimen.cal_arrow_icon_height);
		buttonFont = res.getString(R.string.cal_button_font);
		buttonWidth = res.getDimensionPixelSize(R.dimen.cal_button_width);
		buttonHeight = res.getDimensionPixelSize(R.dimen.cal_button_height);
		buttonTextSize = res.getDimensionPixelSize(R.dimen.cal_button_text_size);
		buttonPadding = res.getDimensionPixelSize(R.dimen.cal_button_padding);
		buttonSpacing = res.getDimensionPixelSize(R.dimen.cal_button_spacing);
		setProperties(attrs);
	}

	private void setProperties(AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
		String _title = ta.getString(R.styleable.CalendarView_cal_title);
		String _titleFont = ta.getString(R.styleable.CalendarView_cal_titleFont);
		String _contentFont = ta.getString(R.styleable.CalendarView_cal_contentFont);
		String _buttonFont = ta.getString(R.styleable.CalendarView_cal_buttonFont);
		title = _title != null ? _title : title;
		titleFont = _titleFont != null ? _titleFont : titleFont;
		titlePadding = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_titlePadding, titlePadding);
		titleTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_titleTextSize, titleTextSize);
		defaultTextColor = ta.getColor(R.styleable.CalendarView_cal_defaultTexColor, defaultTextColor);
		contentFont = _contentFont != null ? _contentFont : contentFont;
		contentTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_contentTextSize, contentTextSize);
		contentPadding = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_contentPadding, contentPadding);
		accentColor = ta.getColor(R.styleable.CalendarView_cal_accentColor, accentColor);
		selectedColor = ta.getColor(R.styleable.CalendarView_cal_selectedColor, selectedColor);
		dateTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_dateTextSize, dateTextSize);
		yearTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_yearTextSize, yearTextSize);
		monthTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_monthTextSize, monthTextSize);
		arrowSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_arrowSize, arrowSize);
		arrowIconWidth = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_arrowIconWidth, arrowIconWidth);
		arrowIconHeight = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_arrowIconHeight, arrowIconHeight);
		buttonFont = _buttonFont != null ? _buttonFont : buttonFont;
		buttonWidth = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_buttonWidth, buttonWidth);
		buttonHeight = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_buttonHeight, buttonHeight);
		buttonTextSize = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_buttonTextSize, buttonTextSize);
		buttonSpacing = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_buttonSpacing, buttonSpacing);
		buttonPadding = ta.getDimensionPixelSize(R.styleable.CalendarView_cal_buttonPadding, buttonPadding);
		ta.recycle();
	}

	private void applyProperties() {
		tvTitleCalendar.setText(title);
		tvTitleCalendar.setTextSize(titleTextSize);
		tvTitleCalendar.setPadding(titlePadding);
		tvTitleCalendar.setTextColor(defaultTextColor);
		tvTitleCalendar.setFont(titleFont);
		btnCancelCalendar.setTextColor(defaultTextColor);
		btnConfirmCalendar.setTextColor(accentColor);
		btnMonthYearCalendar.setFont(titleFont);
		btnMonthYearCalendar.setTextColorPressed(accentColor);
		btnMonthYearCalendar.setTextSize(monthTextSize);
		llDayCalendar.setPadding(contentPadding, 0, contentPadding, 0);
		tvDateCalendar.setFont(contentFont);
		tvDateCalendar.setTextSize(dateTextSize);
		tvYearCalendar.setFont(contentFont);
		tvYearCalendar.setTextSize(yearTextSize);
		flNextCalendar.getLayoutParams().width = arrowSize;
		flNextCalendar.getLayoutParams().height = arrowSize;
		flPreviousCalendar.getLayoutParams().width = arrowSize;
		flPreviousCalendar.getLayoutParams().height = arrowSize;
		vNextCalendar.getLayoutParams().width = arrowIconWidth;
		vNextCalendar.getLayoutParams().height = arrowIconHeight;
		vPreviousCalendar.getLayoutParams().width = arrowIconWidth;
		vPreviousCalendar.getLayoutParams().height = arrowIconHeight;
		btnCancelCalendar.setFont(buttonFont);
		btnCancelCalendar.getLayoutParams().width = buttonWidth;
		btnCancelCalendar.getLayoutParams().height = buttonHeight;
		btnCancelCalendar.setTextSize(buttonTextSize);
		btnConfirmCalendar.setFont(buttonFont);
		btnConfirmCalendar.getLayoutParams().width = buttonWidth;
		btnConfirmCalendar.getLayoutParams().height = buttonHeight;
		btnConfirmCalendar.setTextSize(buttonTextSize);
		findViewById(R.id.vDividerCalendar).setBackgroundColor(accentColor);
		findViewById(R.id.vButtonCalendar).getLayoutParams().width = buttonSpacing;
		findViewById(R.id.llButtonsCalendar).setPadding(buttonPadding,
			buttonPadding, buttonPadding, buttonPadding);
		int count = llDayCalendar.getChildCount();
		for (int i = 0; i < count; i++) {
			CodePanLabel label = (CodePanLabel) llDayCalendar.getChildAt(i);
			label.setFont(contentFont);
			label.setTextSize(contentTextSize);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = inflate(context, R.layout.calendar_view_layout, this);
		tvTitleCalendar = view.findViewById(R.id.tvTitleCalendar);
		tvYearCalendar = view.findViewById(R.id.tvYearCalendar);
		tvDateCalendar = view.findViewById(R.id.tvDateCalendar);
		btnMonthYearCalendar = view.findViewById(R.id.btnMonthYearCalendar);
		btnCancelCalendar = view.findViewById(R.id.btnCancelCalendar);
		btnConfirmCalendar = view.findViewById(R.id.btnConfirmCalendar);
		btnPreviousCalendar = view.findViewById(R.id.btnPreviousCalendar);
		btnNextCalendar = view.findViewById(R.id.btnNextCalendar);
		llDayCalendar = view.findViewById(R.id.llDayCalendar);
		flNextCalendar = view.findViewById(R.id.flNextCalendar);
		flPreviousCalendar = view.findViewById(R.id.flPreviousCalendar);
		vNextCalendar = view.findViewById(R.id.vNextCalendar);
		vPreviousCalendar = view.findViewById(R.id.vPreviousCalendar);
		vpCalendar = view.findViewById(R.id.vpCalendar);
		btnMonthYearCalendar.setOnClickListener(v -> {
			switch (mode) {
				case DAY_MODE:
					mode = MONTH_MODE;
					llDayCalendar.setVisibility(View.GONE);
					monthCalList = getMonthCalList(CURRENT);
					adapter = new ViewPagerAdapter(context, monthCalList);
					vpCalendar.setAdapter(adapter);
					vpCalendar.setCurrentItem(CURRENT, false);
					break;
				case MONTH_MODE:
					mode = YEAR_MODE;
					yearCalList = getYearCalList(CURRENT);
					adapter = new ViewPagerAdapter(context, yearCalList);
					vpCalendar.setAdapter(adapter);
					vpCalendar.setCurrentItem(CURRENT, false);
					break;
			}
		});
		btnPreviousCalendar.setOnClickListener(v -> {
			vpCalendar.setCurrentItem(PREVIOUS, true);
		});
		btnNextCalendar.setOnClickListener(v -> {
			vpCalendar.setCurrentItem(NEXT, true);
		});
		btnConfirmCalendar.setOnClickListener(v -> {
			if (pickDateCallback != null) {
				pickDateCallback.onPickDate(selectedDate);
			}
		});
		btnCancelCalendar.setOnClickListener(v -> {
			if (cancelCallback != null) {
				cancelCallback.onCancel();
			}
		});
		if (date != null) {
			cal = CodePanUtils.getCalendar(date);
			selectedDate = date;
		}
		else {
			cal = Calendar.getInstance();
			selectedDate = CodePanUtils.getDate();
		}
		dayCalList = getDayCalList(lastPosition);
		adapter = new ViewPagerAdapter(context, dayCalList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
		btnMonthYearCalendar.setText(getTitleMonthYear());
		vpCalendar.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				lastPosition = position;
				switch(mode) {
					case DAY_MODE:
						switch(position) {
							case PREVIOUS:
								cal.set(getYear(), getMonth() - 1, getDay());
								break;
							case NEXT:
								cal.set(getYear(), getMonth() + 1, getDay());
								break;
						}
						btnMonthYearCalendar.setText(getTitleMonthYear());
						break;
					case MONTH_MODE:
						switch(position) {
							case PREVIOUS:
								cal.set(getYear() - 1, getMonth(), getDay());
								break;
							case NEXT:
								cal.set(getYear() + 1, getMonth(), getDay());
								break;
						}
						btnMonthYearCalendar.setText(getTitleYear());
						break;
					case YEAR_MODE:
						int count = yearCol * yearRow;
						switch(position) {
							case PREVIOUS:
								cal.set(getYear() - count, getMonth(), getDay());
								break;
							case NEXT:
								cal.set(getYear() + count, getMonth(), getDay());
								break;
						}
						btnMonthYearCalendar.setText(getTitleYearRange());
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE) {
					switchItem(lastPosition);
				}
			}
		});
		displayDate(selectedDate);
		applyProperties();
	}

	private void switchItem(final int position) {
		final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
			ArrayList<View> viewList = new ArrayList<>();
			switch (mode) {
				case DAY_MODE:
					viewList = dayCalList;
					break;
				case MONTH_MODE:
					viewList = monthCalList;
					break;
				case YEAR_MODE:
					viewList = yearCalList;
					break;
			}
			adapter = new ViewPagerAdapter(context, viewList);
			vpCalendar.setAdapter(adapter);
			vpCalendar.setCurrentItem(CURRENT, false);
			return true;
		});
		Thread bg = new Thread(() -> {
			switch (mode) {
				case DAY_MODE:
					dayCalList = getDayCalList(position);
					break;
				case MONTH_MODE:
					monthCalList = getMonthCalList(position);
					break;
				case YEAR_MODE:
					yearCalList = getYearCalList(position);
					break;
			}
			handler.sendMessage(handler.obtainMessage());
		});
		bg.start();
	}

	private ArrayList<View> getDayCalList(final int position) {
		ArrayList<View> viewList = new ArrayList<>();
		switch (position) {
			case PREVIOUS:
				nextCalDay = currCalDay;
				currCalDay = prevCalDay;
				prevCalDay = getPrevCalDay();
				break;
			case CURRENT:
				prevCalDay = getPrevCalDay();
				currCalDay = getCurrCalDay();
				nextCalDay = getNextCalDay();
				break;
			case NEXT:
				prevCalDay = currCalDay;
				currCalDay = nextCalDay;
				nextCalDay = getNextCalDay();
				break;
		}
		viewList.add(prevCalDay);
		viewList.add(currCalDay);
		viewList.add(nextCalDay);
		return viewList;
	}

	private CalendarDay getPrevCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth() - 1, getDay());
		CalendarDay previous = new CalendarDay(context, this);
		previous.init(plotCalendar(cal), this, this);
		return previous;
	}

	private CalendarDay getCurrCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth(), getDay());
		CalendarDay current = new CalendarDay(context, this);
		current.init(plotCalendar(cal), this, this);
		return current;
	}

	private CalendarDay getNextCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth() + 1, getDay());
		CalendarDay next = new CalendarDay(context, this);
		next.init(plotCalendar(cal), this, this);
		return next;
	}

	private ArrayList<DayData> plotCalendar(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		MonthData previous = getMonthDetails(year, month - 1, dayOfMonth);
		MonthData current = getMonthDetails(year, month, dayOfMonth);
		int prevExcess = current.firstDayOfMonth - 1;
		prevExcess = prevExcess == 0 ? 7 : prevExcess;
		int firstElement = previous.noOfDays - prevExcess;
		int limit = previous.noOfDays;
		int day = 0;
		int reset = 0;
		ArrayList<DayData> dayList = new ArrayList<>();
		for (int x = 1; x <= 42; x++) {
			DayData data = new DayData();
			if (x == 1) {
				day = firstElement;
			}
			else {
				if (day == limit) {
					day = 0;
					limit = current.noOfDays;
					reset++;
				}
			}
			day++;
			data.id = day;
			int m = 0;
			switch (reset) {
				case 0:
					if (month == 0) {
						month = 12;
						year -= 1;
					}
					m = month;
					data.isActive = false;
					break;
				case 1:
					if (month == 12) {
						month = 0;
						year += 1;
					}
					m = month + 1;
					data.isActive = true;
					break;
				case 2:
					if (month == 11) {
						month = -1;
						year += 1;
					}
					m = month + 2;
					data.isActive = false;
					break;
			}
			data.date = year + "-" + String.format(Locale.ENGLISH, "%02d", m) + "-" +
				String.format(Locale.ENGLISH, "%02d", day);
			if (data.date.equals(selectedDate)) {
				data.isSelect = true;
			}
			dayList.add(data);
		}
		return dayList;
	}

	private ArrayList<View> getMonthCalList(final int position) {
		ArrayList<View> viewList = new ArrayList<>();
		switch (position) {
			case PREVIOUS:
				nextCalMonth = currCalMonth;
				currCalMonth = prevCalMonth;
				prevCalMonth = getCalMonth();
				break;
			case CURRENT:
				prevCalMonth = getCalMonth();
				currCalMonth = getCalMonth();
				nextCalMonth = getCalMonth();
				break;
			case NEXT:
				prevCalMonth = currCalMonth;
				currCalMonth = nextCalMonth;
				nextCalMonth = getCalMonth();
				break;
		}
		viewList.add(prevCalMonth);
		viewList.add(currCalMonth);
		viewList.add(nextCalMonth);
		return viewList;
	}

	private CalendarMonth getCalMonth() {
		CalendarMonth month = new CalendarMonth(context, this);
		month.init(getMonthList(), this);
		return month;
	}

	private ArrayList<MonthData> getMonthList() {
		ArrayList<MonthData> monthList = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			Calendar cal = Calendar.getInstance();
			cal.set(getYear(), i, 1);
			MonthData month = new MonthData();
			month.id = i;
			month.name = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
					Locale.getDefault());
			monthList.add(month);
		}
		return monthList;
	}

	private ArrayList<View> getYearCalList(final int position) {
		ArrayList<View> viewList = new ArrayList<>();
		switch (position) {
			case PREVIOUS:
				nextCalYear = currCalYear;
				currCalYear = prevCalYear;
				prevCalYear = getPrevCalYear();
				break;
			case CURRENT:
				prevCalYear = getPrevCalYear();
				currCalYear = getCurrCalYear();
				nextCalYear = getNextCalYear();
				break;
			case NEXT:
				prevCalYear = currCalYear;
				currCalYear = nextCalYear;
				nextCalYear = getNextCalYear();
				break;
		}
		viewList.add(prevCalYear);
		viewList.add(currCalYear);
		viewList.add(nextCalYear);
		return viewList;
	}

	private CalendarYear getPrevCalYear() {
		int count = yearCol * yearRow;
		int start = getYear() - count;
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear(context, this);
		year.init(yearList, this);
		return year;
	}

	private CalendarYear getCurrCalYear() {
		int count = yearCol * yearRow;
		int start = getYear();
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear(context, this);
		year.init(yearList, this);
		return year;
	}

	private CalendarYear getNextCalYear() {
		int count = yearCol * yearRow;
		int start = getYear() + count;
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear(context, this);
		year.init(yearList, this);
		return year;
	}

	private ArrayList<YearData> getYearList(int start, int count) {
		int limit = start + count;
		ArrayList<YearData> yearList = new ArrayList<>();
		for (int i = start; i < limit; i++) {
			YearData year = new YearData();
			year.id = i;
			year.name = String.valueOf(i);
			yearList.add(year);
		}
		return yearList;
	}

	public String getTitleMonthYear() {
		String strMonth = cal.getDisplayName(Calendar.MONTH,
			Calendar.LONG, Locale.getDefault());
		return strMonth + " " + cal.get(Calendar.YEAR);
	}

	public String getTitleYear() {
		int year = getYear();
		return String.valueOf(year);
	}

	public String getTitleYearRange() {
		int count = yearCol * yearRow;
		int start = getYear();
		int end = start + count - 1;
		return start + " - " + end;
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH);
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	public int getDay() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public MonthData getMonthDetails(int year, int month, int day) {
		MonthData data = new MonthData();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.set(year, month, 1);
		data.firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
		data.noOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return data;
	}

	public void setOnPickDateCallback(OnPickDateCallback pickDateCallback) {
		this.pickDateCallback = pickDateCallback;
	}

	public void setOnCancelCallback(OnCancelCallback cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

	/**
	 * @param date (yyyy-mm-dd)
	 */
	public void setCurrentDate(String date) {
		this.date = date;
	}

	@Override
	public void onPickDate(String date) {
		this.date = date;
	}

	@Override
	public void onSelectDate(String date) {
		((CalendarDay) dayCalList.get(PREVIOUS)).setSelected(date);
		((CalendarDay) dayCalList.get(NEXT)).setSelected(date);
		this.selectedDate = date;
		displayDate(date);
	}

	public void displayDate(String date) {
		String year = CodePanUtils.getDisplayYear(date);
		String today = CodePanUtils.getReadableDate(date, true, false, true);
		tvYearCalendar.setText(year);
		tvDateCalendar.setText(today);
	}

	@Override
	public void onPickMonth(MonthData month) {
		cal.set(getYear(), month.id, 1);
		mode = DAY_MODE;
		llDayCalendar.setVisibility(View.VISIBLE);
		dayCalList = getDayCalList(CURRENT);
		adapter = new ViewPagerAdapter(context, dayCalList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
	}

	@Override
	public void onPickYear(YearData year) {
		cal.set(year.id, getMonth(), 1);
		mode = MONTH_MODE;
		monthCalList = getMonthCalList(CURRENT);
		adapter = new ViewPagerAdapter(context, monthCalList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
	}

	public String getContentFont() {
		return this.contentFont;
	}

	public int getDefaultTextColor() {
		return this.defaultTextColor;
	}

	public int getContentTextSize() {
		return this.contentTextSize;
	}

	public int getSelectedColor() {
		return this.selectedColor;
	}

	public int getContentPadding() {
		return this.contentPadding;
	}
}
