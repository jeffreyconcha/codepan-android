package com.codepan.utils;

import android.util.Log;

import com.codepan.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Console {

	private static final int CHAR_LIMIT = 4096;
	private static final int INDENT = 4;
	private static final String TAG = "CONSOLE";
	private static final String URL = TAG + " URL";
	private static final String PARAMS = TAG + " PARAMS";
	private static final String RESPONSE = TAG + " RESPONSE";

	public static void info(String input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void info(int input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void info(double input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void info(long input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void info(float input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void info(boolean input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void error(String input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void error(int input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void error(double input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void error(long input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void error(boolean input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void error(float input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void debug(String input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void debug(int input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void debug(double input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void debug(long input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void debug(float input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void debug(boolean input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void log(String input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void log(int input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void log(double input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void log(long input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void log(float input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void log(boolean input) {
		if(BuildConfig.DEBUG) {
			Log.i(TAG, String.valueOf(input));
		}
	}

	public static void logUrl(String url) {
		debugLog(URL, url);
	}

	public static void logParams(String params) {
		try {
			JSONObject json = new JSONObject(params);
			largeLog(PARAMS, json.toString(INDENT));
		}
		catch(JSONException je) {
			je.printStackTrace();
			try {
				JSONArray array = new JSONArray(params);
				largeLog(PARAMS, array.toString(INDENT));
			}
			catch(JSONException e) {
				e.printStackTrace();
				largeLog(PARAMS, params);
			}
		}
	}

	public static void logResponse(String response) {
		try {
			JSONObject json = new JSONObject(response);
			largeLog(RESPONSE, json.toString(INDENT));
		}
		catch(JSONException je) {
			je.printStackTrace();
			try {
				JSONArray array = new JSONArray(response);
				largeLog(RESPONSE, array.toString(INDENT));
			}
			catch(JSONException e) {
				e.printStackTrace();
				largeLog(RESPONSE, response);
			}
		}
	}

	public static void largeLog(String tag, String content) {
		if(content.length() > CHAR_LIMIT) {
			debugLog(tag, content.substring(0, CHAR_LIMIT));
			appendLog(content.substring(CHAR_LIMIT));
		}
		else {
			debugLog(tag, content);
		}
	}

	private static void appendLog(String content) {
		if(content.length() > CHAR_LIMIT) {
			debugLog(null, content.substring(0, CHAR_LIMIT));
//			appendLog(content.substring(CHAR_LIMIT));
		}
		else {
			debugLog(null, content);
		}
	}

	private static void debugLog(String tag, String data) {
		if(BuildConfig.DEBUG) {
			Log.i(tag, data);
		}
	}
}
