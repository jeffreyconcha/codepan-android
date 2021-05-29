package com.codepan.net;

import com.codepan.utils.Console;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ErrorHandler {

	private String[] keys = {
			"message",
			"error"
	};
	private InputStream is;
	private String raw;

	/**
	 * @param is - Error stream from connection.getErrorStream()
	 */
	public String getErrorMessage(InputStream is) throws IOException {
		StringBuilder error = new StringBuilder();
		Reader in = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(in);
		String line;
		while((line = reader.readLine()) != null) {
			error.append(line);
		}
		reader.close();
		this.raw = error.toString();
		Console.log(raw);
		if(!raw.isEmpty()) {
			try {
				JSONObject json = new JSONObject(raw);
				for(String key : keys) {
					if(json.has(key)) {
						return json.getString(key);
					}
				}
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return getDefaultMessage();
	}

	public String getRaw() {
		return this.raw;
	}

	public String getDefaultMessage() {
		return "Unable to connect to server.";
	}

	public String getWeakInternetMessage() {
		return "You are getting weak internet connection. " +
				"Please find a reliable source to continue.";
	}

	public String getTimeOutMessage() {
		return "Connection timed out, the server is taking too long to respond. " +
				"Please check your internet connection and try again.";
	}

	public String getInvalidURLMessage() {
		return "URL is not valid.";
	}

	public String getRedirectedMessage() {
		return "There was a problem in your internet connection. Please check and try again.";
	}

	public String getConnectionLostMessage() {
		return "Connection to server lost.";
	}

	public String getDirectoryMessage() {
		return "Unable to create a directory.";
	}
}
