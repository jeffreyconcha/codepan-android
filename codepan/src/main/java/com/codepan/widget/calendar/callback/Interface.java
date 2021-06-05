package com.codepan.widget.calendar.callback;

import com.codepan.widget.calendar.model.MonthData;
import com.codepan.widget.calendar.model.YearData;
public class Interface {

	public interface OnPickDateCallback {
		void onPickDate(String date);
	}

	public interface OnPickMonthCallback {
		void onPickMonth(MonthData month);
	}

	public interface OnPickYearCallback {
		void onPickYear(YearData year);
	}

	public interface OnSelectDateCallback {
		void onSelectDate(String date);
	}
}
