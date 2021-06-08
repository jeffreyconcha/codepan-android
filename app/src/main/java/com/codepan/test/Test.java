package com.codepan.test;

import android.content.Context;

import com.codepan.time.DateTime;
import com.codepan.utils.Console;

public class Test {

	public static void run(Context context) {
		Console.log(DateTime.Companion.now());
	}
}
