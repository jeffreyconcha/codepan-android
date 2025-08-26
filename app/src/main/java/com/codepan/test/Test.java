package com.codepan.test;

import android.content.Context;

import com.codepan.time.DateTime;
import com.codepan.time.DateTimeRange;
import com.codepan.utils.Console;
public class Test {

	public static void run(Context context) {
		DateTime today = DateTime.Companion.today();
		for(DateTimeRange.Period period: DateTimeRange.Period.getEntries()) {
			DateTimeRange range = today.toPeriod(period);
			Console.log(range);
		}
	}
}
