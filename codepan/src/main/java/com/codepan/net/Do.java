package com.codepan.net;

import android.content.Context;

import com.codepan.net.Callback.OnDownloadFileCallback;
import com.codepan.utils.Console;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;

public class Do {

	private static final int INDENT = 4;

	public static String httpGet(String url, JSONObject paramsObj, Authorization authorization,
								 boolean encode, int timeOut) {
		String params = "";
		if (paramsObj != null) {
			params = "?";
			Iterator<String> iterator = paramsObj.keys();
			try {
				int i = 0;
				while (iterator.hasNext()) {
					String key = iterator.next();
					String text = paramsObj.getString(key);
					String value = encode ? URLEncoder.encode(text, "UTF-8") : text;
					if (i != 0) {
						params += "&" + key + "=" + value;
					}
					else {
						params += key + "=" + value;
					}
					i++;
				}
				Console.logUrl(url + params);
				Console.logParams(paramsObj.toString(INDENT));
			}
			catch (JSONException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		else {
			Console.logUrl(url);
		}
		return getHttpsResponse(url, params, authorization, timeOut, "GET");
	}

	public static String httpPost(String url, JSONObject paramsObj,
								  Authorization authorization, int timeOut) {
		Console.logUrl(url);
		try {
			Console.logParams(paramsObj.toString(INDENT));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return getHttpsResponse(url, paramsObj.toString(), authorization, timeOut, "POST");
	}

	private static String getHttpsResponse(String host, String params, Authorization authorization,
										   int timeOut, String method) {
		boolean result = false;
		StringBuilder response = new StringBuilder();
		String exception = null;
		String message = null;
		ErrorHandler handler = new ErrorHandler();
		boolean doOutput = method != null && method.equals("POST");
		String uri = method != null && method.equals("GET") ? host + params : host;
		String contentType = method != null && method.equals("GET") ?
			"application/x-www-form-urlencoded" : "application/json";
		try {
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (connection instanceof HttpsURLConnection) {
				HttpsURLConnection https = (HttpsURLConnection) connection;
				https.setSSLSocketFactory(new TLSSocketFactory());
				connection = https;
			}
			try {
				connection.setConnectTimeout(timeOut);
				connection.setReadTimeout(timeOut);
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", contentType);
				connection.setRequestProperty("Content-Language", "en-US");
				connection.setRequestProperty("Connection", "close");
				connection.setRequestProperty("Accept-Encoding", "");
				if (authorization != null) {
					connection.setRequestProperty("Authorization", authorization.getAuthorization());
				}
				connection.setDoInput(true);
				connection.setDoOutput(doOutput);
				connection.setUseCaches(false);
				connection.connect();
				if (method != null && method.equals("POST")) {
					Writer out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
					BufferedWriter writer = new BufferedWriter(out);
					writer.write(params);
					writer.flush();
					writer.close();
				}
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					if (!url.getHost().equals(connection.getURL().getHost())) {
						message = handler.getRedirectedMessage();
					}
					else {
						Reader in = new InputStreamReader(connection.getInputStream());
						BufferedReader reader = new BufferedReader(in);
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						reader.close();
						result = true;
					}
					Console.logResponse(response.toString());
				}
				else {
					InputStream error = connection.getErrorStream();
					message = handler.getErrorMessage(error);
					Console.logResponse(handler.getRaw());
				}
			}
			catch (SSLProtocolException spe) {
				spe.printStackTrace();
				return getHttpsResponse(host, params,
					authorization, timeOut, method);
			}
			catch (SocketTimeoutException ste) {
				ste.printStackTrace();
				exception = ste.toString();
				message = handler.getTimeOutMessage();
			}
			catch (UnknownHostException he) {
				exception = he.toString();
				message = handler.getWeakInternetMessage();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				exception = ioe.toString();
				message = handler.getDefaultMessage();
			}
			catch (Exception e) {
				e.printStackTrace();
				exception = e.toString();
				message = e.getMessage();
			}
			finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		catch (MalformedURLException mue) {
			mue.printStackTrace();
			exception = mue.toString();
			message = handler.getInvalidURLMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if (!result) {
			try {
				JSONObject field = new JSONObject();
				JSONObject error = new JSONObject();
				error.put("type", "android");
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response.append(error.toString());
			}
			catch (JSONException je) {
				je.printStackTrace();
			}
		}
		return response.toString();
	}

	public static void downloadFile(Context context, String uri, String folder,
									String fileName, boolean external, Cipher cipher,
									OnDownloadFileCallback callback) {
		ErrorHandler handler = new ErrorHandler();
		try {
			final int TIMEOUT = 10000;
			String ex = context.getExternalFilesDir(null).getPath() + "/" + folder;
			File dir = external ? new File(ex) : context.getDir(folder, Context.MODE_PRIVATE);
			boolean result = dir.exists() || dir.mkdir();
			if (result) {
				URL url = new URL(uri);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				if (connection instanceof HttpsURLConnection) {
					HttpsURLConnection https = (HttpsURLConnection) connection;
					https.setSSLSocketFactory(new TLSSocketFactory());
					connection = https;
				}
				connection.setConnectTimeout(TIMEOUT);
				connection.setReadTimeout(TIMEOUT);
				String path = dir.getPath() + "/" + fileName;
				File file = new File(path);
				int downloaded = 0;
				if (file.exists()) {
					downloaded = (int) file.length();
					String range = "bytes=" + downloaded + "-";
					connection.setRequestProperty("Range", range);
				}
				else {
					file.createNewFile();
				}
				connection.connect();
				int max = connection.getContentLength() + downloaded;
				InputStream input = connection.getInputStream();
				OutputStream output = new FileOutputStream(file, downloaded > 0);
				CipherOutputStream cos = null;
				if (cipher != null) {
					cos = new CipherOutputStream(output, cipher);
				}
				int count;
				int progress = downloaded;
				byte[] data = new byte[1024];
				while ((count = input.read(data)) > 0) {
					progress += count;
					if (cipher != null) {
						cos.write(data, 0, count);
					}
					else {
						output.write(data, 0, count);
					}
					if (callback != null) {
						callback.onProgress(progress, max);
					}
				}
				if (cipher != null) {
					cos.flush();
					cos.close();
				}
				else {
					output.flush();
					output.close();
				}
				input.close();
				if (callback != null) {
					callback.onComplete();
				}
				connection.disconnect();
			}
			else {
				if (callback != null) {
					callback.onError(handler.getDirectoryMessage());
				}
			}
		}
		catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			if (callback != null) {
				callback.onError(handler.getTimeOutMessage());
			}
		}
		catch (SSLException s) {
			s.printStackTrace();
			if (callback != null) {
				callback.onError(handler.getConnectionLostMessage());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			if (callback != null) {
				String error = e.getMessage();
				callback.onError(error);
			}
		}
	}

	public static String uploadFile(String url, JSONObject json, Authorization authorization,
									String name, File file) {
		String response = null;
		String message = null;
		String exception = null;
		final int INDENT = 4;
		boolean result = false;
		ErrorHandler handler = new ErrorHandler();
		try {
			Multipart multipart = new Multipart(url, "UTF-8");
			if (authorization != null) {
				multipart.addHeaderField("Authorization",
					authorization.getAuthorization());
			}
			try {
				Iterator<String> keys = json.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = json.getString(key);
					multipart.addFormField(key, value);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (file != null && file.exists()) {
				multipart.addFilePart(name, file);
			}
			response = multipart.finish(handler);
			result = true;
		}
		catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			exception = ste.toString();
			message = handler.getTimeOutMessage();
		}
		catch (UnknownHostException he) {
			exception = he.toString();
			message = handler.getWeakInternetMessage();
		}
		catch (IOException e) {
			exception = e.toString();
			message = handler.getDefaultMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if (!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch (JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}

	public static String uploadFile(String url, String json, Authorization authorization,
									String name, File file) {
		String response = null;
		String message = null;
		String exception = null;
		final int INDENT = 4;
		boolean result = false;
		ErrorHandler handler = new ErrorHandler();
		Console.logUrl(url);
		Console.logParams(json);
		try {
			Multipart multipart = new Multipart(url, "UTF-8");
			if (authorization != null) {
				multipart.addHeaderField("Authorization",
					authorization.getAuthorization());
			}
			multipart.addFormField("params", json);
			multipart.addFilePart(name, file);
			response = multipart.finish(handler);
			Console.logResponse(response);
			result = true;
		}
		catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			exception = ste.toString();
			message = handler.getTimeOutMessage();
		}
		catch (UnknownHostException he) {
			exception = he.toString();
			message = handler.getWeakInternetMessage();
		}
		catch (IOException e) {
			exception = e.toString();
			message = handler.getDefaultMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if (!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch (JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}
}
