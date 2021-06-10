package com.codepan.test;

import android.content.Context;

import com.codepan.time.DateTime;
import com.codepan.utils.Console;

import java.util.TimeZone;

public class Test {

	public static void run(Context context) {
		DateTime dt1 = new DateTime("2020-04-02", "00:22:11", TimeZone.getDefault());
		DateTime dt2 = new DateTime("2020-06-02", "00:00:00", TimeZone.getDefault());
		DateTime now = DateTime.Companion.now();
		DateTime other = now.to(TimeZone.getTimeZone("Asia/Singapore"));
		Console.log(now.equals(other));
		Console.log(dt1.isBefore(dt2));
	}
}
