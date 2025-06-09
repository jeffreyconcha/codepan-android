package com.codepan.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

public class Multipart {

	private final String BOUNDARY = "***BOUNDARY***";
	private final String LINE_FEED = "\r\n";
	private HttpURLConnection connection;
	private DataOutputStream out;
	private OutputStream os;
	private String charset;

	public Multipart(String uri, String charset) throws IOException,
		NoSuchAlgorithmException, KeyManagementException {
		this.charset = charset;
		String type = "multipart/form-data; boundary=" + BOUNDARY;
		URL url = new URL(uri);
		connection = (HttpURLConnection) url.openConnection();
		if(connection instanceof HttpsURLConnection) {
			HttpsURLConnection https = (HttpsURLConnection) connection;
			https.setSSLSocketFactory(new TLSSocketFactory());
			connection = https;
		}
		connection.setRequestProperty("Content-Type", type);
		connection.setReadTimeout(120000);
		connection.setConnectTimeout(120000);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		Log.e("URL", uri);
	}

	public void connect() throws IOException {
		if(connection != null) {
			connection.connect();
			os = connection.getOutputStream();
			out = new DataOutputStream(os);
		}
	}

	public void addHeaderField(String name, String value) throws IOException {
		connection.setRequestProperty(name, value);
	}

	public void addFormField(String name, String value) throws IOException {
		out.writeBytes("--" + BOUNDARY);
		out.writeBytes(LINE_FEED);
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		out.writeBytes(LINE_FEED);
		out.writeBytes("Content-Type: text/plain; charset=" + charset);
		out.writeBytes(LINE_FEED);
		out.writeBytes(LINE_FEED);
		out.writeBytes(value);
		out.writeBytes(LINE_FEED);
		out.flush();
	}

	public void addFilePart(String name, File file) throws IOException {
		String fileName = file.getName();
		String mimeType = URLConnection.guessContentTypeFromName(fileName);
		out.writeBytes("--" + BOUNDARY);
		out.writeBytes(LINE_FEED);
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; " +
			"filename=\"" + fileName + "\"");
		out.writeBytes(LINE_FEED);
		out.writeBytes("Content-Type: " + mimeType);
		out.writeBytes(LINE_FEED);
		out.writeBytes("Content-Transfer-Encoding: binary");
		out.writeBytes(LINE_FEED);
		out.writeBytes(LINE_FEED);
		out.flush();
		FileInputStream is = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int line;
		while((line = is.read(buffer)) != -1) {
			os.write(buffer, 0, line);
		}
		os.flush();
		is.close();
		out.writeBytes(LINE_FEED);
		out.flush();
	}

	public String finish(ErrorHandler handler) throws IOException {
		boolean result = false;
		String message = null;
		StringBuilder builder = new StringBuilder();
		out.writeBytes(LINE_FEED);
		out.writeBytes("--" + BOUNDARY + "--");
		out.writeBytes(LINE_FEED);
		out.flush();
		out.close();
		if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			Reader in = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader(in);
			String line;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			result = true;
		}
		else {
			InputStream error = connection.getErrorStream();
			message = handler.getErrorMessage(error);
		}
		if(!result) {
			try {
				JSONObject field = new JSONObject();
				JSONObject error = new JSONObject();
				error.put("type", "android");
				field.put("message", message);
				error.put("error", field);
				builder.append(error.toString());
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return builder.toString();
	}
}
