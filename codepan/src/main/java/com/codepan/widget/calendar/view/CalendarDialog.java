package com.codepan.widget.calendar.view;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.codepan.R;
import com.codepan.adapter.FragmentPagerAdapter;
import com.codepan.app.CPFragment;
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

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import static com.codepan.widget.calendar.callback.Interface.OnPickDateCallback;

public class CalendarDialog extends CPFragment implements OnPickDateCallback, OnSelectDateCallback,
		OnPickMonthCallback, OnPickYearCallback {

	private final int PREVIOUS = 0;
	private final int CURRENT = 1;
	private final int NEXT = 2;
	private final int DAY_MODE = 0;
	private final int MONTH_MODE = 1;
	private final int YEAR_MODE = 2;
	private int lastPosition = CURRENT;
	private int mode = DAY_MODE;
	private CodePanLabel tvYearCalendar, tvDateCalendar;
	private CodePanButton btnCancelCalendar, btnSaveCalendar, btnMonthYearCalendar;
	private int height, monthRow, monthCol, yearRow, yearCol, spacing;
	private Button btnPreviousCalendar, btnNextCalendar;
	private OnPickDateCallback pickDateCallback;
	private ArrayList<CPFragment> dayCalList;
	private ArrayList<CPFragment> yearCalList;
	private ArrayList<CPFragment> monthCalList;
	private LinearLayout llDayCalendar;
	private String date, selectedDate;
	private FragmentPagerAdapter adapter;
	private CalendarPager vpCalendar;
	private CalendarMonth prevCalMonth;
	private CalendarMonth currCalMonth;
	private CalendarMonth nextCalMonth;
	private CalendarYear prevCalYear;
	private CalendarYear currCalYear;
	private CalendarYear nextCalYear;
	private CalendarDay prevCalDay;
	private CalendarDay currCalDay;
	private CalendarDay nextCalDay;
	private Calendar cal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.disableBackPressed();
		Resources res = getResources();
		spacing = res.getDimensionPixelSize(R.dimen.cal_spacing);
		monthRow = res.getInteger(R.integer.month_row);
		monthCol = res.getInteger(R.integer.month_col);
		yearRow = res.getInteger(R.integer.year_row);
		yearCol = res.getInteger(R.integer.year_col);
		cal = Calendar.getInstance();
		if(date != null) {
			cal = CodePanUtils.getCalendar(date);
			selectedDate = date;
		}
		else {
			selectedDate = CodePanUtils.getDate();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_dialog_layout, container, false);
		tvYearCalendar = view.findViewById(R.id.tvYearCalendar);
		tvDateCalendar = view.findViewById(R.id.tvDateCalendar);
		btnMonthYearCalendar = view.findViewById(R.id.btnMonthYearCalendar);
		btnCancelCalendar = view.findViewById(R.id.btnCancelCalendar);
		btnSaveCalendar = view.findViewById(R.id.btnSaveCalendar);
		btnPreviousCalendar = view.findViewById(R.id.btnPreviousCalendar);
		btnNextCalendar = view.findViewById(R.id.btnNextCalendar);
		llDayCalendar = view.findViewById(R.id.llDayCalendar);
		vpCalendar = view.findViewById(R.id.vpCalendar);
		btnMonthYearCalendar.setOnClickListener(v -> {
			switch(mode) {
				case DAY_MODE:
					mode = MONTH_MODE;
					int tHeight = llDayCalendar.getHeight() + vpCalendar.getHeight();
					height = (tHeight / monthRow) - spacing;
					vpCalendar.getLayoutParams().height = tHeight;
					llDayCalendar.setVisibility(View.GONE);
					monthCalList = getMonthCalList(CURRENT);
					adapter = new FragmentPagerAdapter(getChildFragmentManager(), monthCalList);
					vpCalendar.setAdapter(adapter);
					vpCalendar.setCurrentItem(CURRENT, false);
					break;
				case MONTH_MODE:
					mode = YEAR_MODE;
					yearCalList = getYearCalList(CURRENT);
					adapter = new FragmentPagerAdapter(getChildFragmentManager(), yearCalList);
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
		btnSaveCalendar.setOnClickListener(v -> {
			manager.popBackStack();
			if (pickDateCallback != null) {
				pickDateCallback.onPickDate(selectedDate);
			}
		});
		btnCancelCalendar.setOnClickListener(v -> {
			manager.popBackStack();
		});
		dayCalList = getDayCalList(lastPosition);
		adapter = new FragmentPagerAdapter(getChildFragmentManager(), dayCalList);
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
				if(state == ViewPager.SCROLL_STATE_IDLE) {
					switchItem(lastPosition);
				}
			}
		});
		displayDate(selectedDate);
		return view;
	}

	public void switchItem(final int position) {
		final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
			ArrayList<CPFragment> fragmentList = new ArrayList<>();
			switch (mode) {
				case DAY_MODE:
					fragmentList = dayCalList;
					break;
				case MONTH_MODE:
					fragmentList = monthCalList;
					break;
				case YEAR_MODE:
					fragmentList = yearCalList;
					break;
			}
			adapter = new FragmentPagerAdapter(getChildFragmentManager(), fragmentList);
			vpCalendar.setAdapter(adapter);
			vpCalendar.setCurrentItem(CURRENT, false);
			return true;
		});
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
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
			}
		});
		bg.start();
	}

	public ArrayList<CPFragment> getDayCalList(final int position) {
		ArrayList<CPFragment> fragmentList = new ArrayList<>();
		switch(position) {
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
		fragmentList.add(prevCalDay);
		fragmentList.add(currCalDay);
		fragmentList.add(nextCalDay);
		return fragmentList;
	}

	public CalendarDay getPrevCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth() - 1, getDay());
		CalendarDay previous = new CalendarDay();
		previous.init(plotCalendar(cal), this, this);
		return previous;
	}

	public CalendarDay getCurrCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth(), getDay());
		CalendarDay current = new CalendarDay();
		current.init(plotCalendar(cal), this, this);
		return current;
	}

	public CalendarDay getNextCalDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(), getMonth() + 1, getDay());
		CalendarDay next = new CalendarDay();
		next.init(plotCalendar(cal), this, this);
		return next;
	}

	public ArrayList<DayData> plotCalendar(Calendar cal) {
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
		ArrayList<DayData> dayList = new ArrayList<DayData>();
		for(int x = 1; x <= 42; x++) {
			DayData obj = new DayData();
			if(x == 1) {
				day = firstElement;
			}
			else {
				if(day == limit) {
					day = 0;
					limit = current.noOfDays;
					reset++;
				}
			}
			day++;
			obj.ID = day;
			int m = 0;
			switch(reset) {
				case 0:
					if(month == 0) {
						month = 12;
						year -= 1;
					}
					m = month;
					obj.isActive = false;
					break;
				case 1:
					if(month == 12) {
						month = 0;
						year += 1;
					}
					m = month + 1;
					obj.isActive = true;
					break;
				case 2:
					if(month == 11) {
						month = -1;
						year += 1;
					}
					m = month + 2;
					obj.isActive = false;
					break;
			}
			obj.date = year + "-" + String.format(Locale.ENGLISH, "%02d", m) + "-" +
					String.format(Locale.ENGLISH, "%02d", day);
			if(obj.date.equals(selectedDate)) {
				obj.isSelect = true;
			}
			dayList.add(obj);
		}
		return dayList;
	}

	public ArrayList<CPFragment> getMonthCalList(final int position) {
		ArrayList<CPFragment> fragmentList = new ArrayList<>();
		switch(position) {
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
		fragmentList.add(prevCalMonth);
		fragmentList.add(currCalMonth);
		fragmentList.add(nextCalMonth);
		return fragmentList;
	}

	public CalendarMonth getCalMonth() {
		CalendarMonth month = new CalendarMonth();
		month.init(getMonthList(), height, this);
		return month;
	}

	public ArrayList<MonthData> getMonthList() {
		ArrayList<MonthData> monthList = new ArrayList<>();
		for(int i = 0; i < 12; i++) {
			Calendar cal = Calendar.getInstance();
			cal.set(getYear(), i, 1);
			MonthData month = new MonthData();
			month.ID = i;
			month.name = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
					Locale.getDefault());
			monthList.add(month);
		}
		return monthList;
	}

	public ArrayList<CPFragment> getYearCalList(final int position) {
		ArrayList<CPFragment> fragmentList = new ArrayList<>();
		switch(position) {
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
		fragmentList.add(prevCalYear);
		fragmentList.add(currCalYear);
		fragmentList.add(nextCalYear);
		return fragmentList;
	}

	public CalendarYear getPrevCalYear() {
		int count = yearCol * yearRow;
		int start = getYear() - count;
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear();
		year.init(yearList, height, this);
		return year;
	}

	public CalendarYear getCurrCalYear() {
		int count = yearCol * yearRow;
		int start = getYear();
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear();
		year.init(yearList, height, this);
		return year;
	}

	public CalendarYear getNextCalYear() {
		int count = yearCol * yearRow;
		int start = getYear() + count;
		ArrayList<YearData> yearList = getYearList(start, count);
		CalendarYear year = new CalendarYear();
		year.init(yearList, height, this);
		return year;
	}

	public ArrayList<YearData> getYearList(int start, int count) {
		int limit = start + count;
		ArrayList<YearData> yearList = new ArrayList<>();
		for(int i = start; i < limit; i++) {
			YearData year = new YearData();
			year.ID = i;
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
		MonthData obj = new MonthData();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.set(year, month, 1);
		obj.firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
		obj.noOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return obj;
	}

	public void setOnPickDateCallback(OnPickDateCallback pickDateCallback) {
		this.pickDateCallback = pickDateCallback;
	}

	/**
	 * @param date (yyyy-mm-dd)
	 */
	public void setCurrentDate(String date) {
		this.date = date;
	}

	public String getDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return year + "-" + String.format(Locale.ENGLISH, "%02d", (month + 1)) + "-" +
				String.format(Locale.ENGLISH, "%02d", day);
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
		cal.set(getYear(), month.ID, getDay());
		mode = DAY_MODE;
		vpCalendar.reset();
		llDayCalendar.setVisibility(View.VISIBLE);
		dayCalList = getDayCalList(CURRENT);
		adapter = new FragmentPagerAdapter(getChildFragmentManager(), dayCalList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
	}

	@Override
	public void onPickYear(YearData year) {
		cal.set(year.ID, getMonth(), getDay());
		mode = MONTH_MODE;
		monthCalList = getMonthCalList(CURRENT);
		adapter = new FragmentPagerAdapter(getChildFragmentManager(), monthCalList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
	}
}
