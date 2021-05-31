package com.codepan.utils;

import android.util.Log;

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

	public static void verbose(Object input) {
		Log.v(TAG, String.valueOf(input));
	}

	public static void debug(Object input) {
		Log.d(TAG, String.valueOf(input));
	}

	public static void info(Object input) {
		Log.i(TAG, String.valueOf(input));
	}

	public static void warn(Object input) {
		Log.w(TAG, String.valueOf(input));
	}

	public static void error(Object input) {
		Log.e(TAG, String.valueOf(input));
	}

	public static void log(Object input) {
		info(input);
	}

	public static void log(Object input, boolean isDebug) {
		if (isDebug) {
			info(input);
		}
	}

	public static void logUrl(String url) {
		appendLog(URL, url);
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
			appendLog(tag, content.substring(0, CHAR_LIMIT));
			appendLog(content.substring(CHAR_LIMIT));
		}
		else {
			appendLog(tag, content);
		}
	}

	private static void appendLog(String content) {
		if (content.length() > CHAR_LIMIT) {
			appendLog(null, content.substring(0, CHAR_LIMIT));
		}
		else {
			appendLog(null, content);
		}
	}

	private static void appendLog(String tag, String data) {
		Log.i(tag, data);
	}
}
