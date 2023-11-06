package com.codepan.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.codepan.net.Callback.OnDownloadFileCallback;
import com.codepan.utils.Console;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.StreamResetException;

public class HttpRequest {

	private static final int MAX_RETRY = 5;
	private static final int INDENT = 4;
	private static final String POST = "POST";
	private static final String GET = "GET";

	private Context context;
	private Authorization authorization;
	private int retryCount = 0;
	private int initialTimeOut, currentTimeOut;
	private String url;

	public HttpRequest(
		Context context,
		String url,
		Authorization authorization,
		int timeOut
	) {
		this.context = context;
		this.authorization = authorization;
		this.initialTimeOut = timeOut;
		this.currentTimeOut = timeOut;
		this.url = url;
	}

	private String getUserAgent() {
		final PackageManager pm = context.getPackageManager();
		if(pm != null) {
			try {
				String packageId = context.getPackageName();
				PackageInfo pi = pm.getPackageInfo(packageId, 0);
				ApplicationInfo ai = pm.getApplicationInfo(packageId, 0);
				String appName = pm.getApplicationLabel(ai).toString();
				return appName + "/" + pi.versionName + "+" + pi.versionCode + "(" +
					"Android " + Build.VERSION.RELEASE + "; " +
					"Model:" + Build.MODEL + "; " + Build.FINGERPRINT + ")";
			}
			catch(PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String get(JSONObject paramsObj, boolean encode) {
		StringBuilder params = new StringBuilder("?");
		if(paramsObj != null) {
			Iterator<String> iterator = paramsObj.keys();
			try {
				int i = 0;
				while(iterator.hasNext()) {
					String key = iterator.next();
					String text = paramsObj.getString(key);
					String encoded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
						URLEncoder.encode(text, StandardCharsets.UTF_8) :
						URLEncoder.encode(text, "UTF-8");
					String value = encode ? encoded : text;
					if(i != 0) {
						params.append("&").append(key).append("=").append(value);
					}
					else {
						params.append(key).append("=").append(value);
					}
					i++;
				}
				Console.logUrl(url + params);
				Console.logParams(paramsObj.toString(INDENT));
			}
			catch(JSONException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		else {
			Console.logUrl(url);
		}
		return getHttpResponse(url, params.toString(), GET);
	}

	public String post(JSONObject paramsObj) {
		Console.logUrl(url);
		try {
			Console.logParams(paramsObj.toString(INDENT));
		}
		catch(JSONException e) {
			e.printStackTrace();
		}
		return getHttpResponse(url, paramsObj.toString(), POST);
	}

	private String getHttpResponse(
		String host,
		String params,
		String method
	) {
		return getNativeHttpsResponse(host, params, method);
//		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//			return getOkHttpsResponse(host, params, method);
//		}
//		else {
//			return getNativeHttpsResponse(host, params, method);
//		}
	}

	private String retry(
		String host,
		String params,
		String method
	)
		throws RuntimeException, InterruptedException {
		Thread.sleep(3000L);
		if(retryCount++ < MAX_RETRY) {
			final int remaining = MAX_RETRY - retryCount;
			currentTimeOut = retryCount * initialTimeOut;
			Console.verbose("REMAINING RETRIES: " + remaining);
			return getHttpResponse(host, params, method);
		}
		throw new RuntimeException("Max retries for HTTP request has been reached!!!");
	}

	private String getOkHttpsResponse(
		String host,
		String params,
		String method
	) {
		StringBuilder builder = new StringBuilder();
		boolean result = false;
		String exception = null;
		String message = null;
		ErrorHandler handler = new ErrorHandler();
		String url = method != null && method.equals(GET) ? host + params : host;
		String contentType = method != null && method.equals(GET) ?
			"application/x-www-form-urlencoded" : "application/json";
		try {
			OkHttpClient client = new OkHttpClient.Builder()
				.protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
				.connectTimeout(currentTimeOut, TimeUnit.MILLISECONDS)
				.sslSocketFactory(new TLSSocketFactory(), new TrustManager())
				.readTimeout(currentTimeOut, TimeUnit.MILLISECONDS)
				.build();
			Request.Builder rb = new Request.Builder()
				.addHeader("Content-Type", contentType)
				.addHeader("Content-Language", "en-US")
				.addHeader("Connection", "close")
				.addHeader("Accept-Encoding", "")
				.url(url);
			if(authorization != null) {
				rb.addHeader("Authorization", authorization.getAuthorization());
			}
			String userAgent = getUserAgent();
			if(userAgent != null) {
				rb.addHeader("User-Agent", userAgent);
			}
			if(method != null && method.equals(POST)) {
				RequestBody body = RequestBody.create(params, MediaType.get("application/json"));
				rb.post(body);
			}
			Request request = rb.build();
			Console.verbose("===== REQUEST HEADERS =====");
			for(String name : request.headers().names()) {
				Console.verbose(name + ": " + request.header(name));
			}
			Console.verbose("===========================");
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()) {
				final ResponseBody body = response.body();
				if(body != null) {
					builder.append(body.string());
				}
				Console.verbose("===== RESPONSE HEADERS =====");
				for(String name : response.headers().names()) {
					Console.verbose(name + ": " + response.header(name));
				}
				Console.verbose("============================");
				Console.logResponse(builder.toString());
				result = true;
			}
			else {
				Console.log("RESPONSE ERROR CODE: " + response.code());
			}
		}
		catch(SSLProtocolException |
			  EOFException |
			  SocketTimeoutException |
			  StreamResetException e) {
			e.printStackTrace();
			try {
				return retry(host, params, method);
			}
			catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		catch(SocketException se) {
			se.printStackTrace();
			String error = se.getMessage();
			if(error != null && error.contains("close")) {
				try {
					return retry(host, params, method);
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
				exception = se.toString();
				message = handler.getTimeOutMessage();
			}
		}
		catch(UnknownHostException he) {
			exception = he.toString();
			message = handler.getWeakInternetMessage();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			exception = ioe.toString();
			message = handler.getDefaultMessage();
		}
		catch(Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if(!result) {
			try {
				JSONObject field = new JSONObject();
				JSONObject error = new JSONObject();
				error.put("type", "android");
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				builder.append(error);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return builder.toString();
	}

	private String getNativeHttpsResponse(
		String host,
		String params,
		String method
	) {
		boolean result = false;
		StringBuilder response = new StringBuilder();
		String exception = null;
		String message = null;
		ErrorHandler handler = new ErrorHandler();
		boolean doOutput = method != null && method.equals(POST);
		String uri = method != null && method.equals(GET) ? host + params : host;
		String contentType = method != null && method.equals(GET) ?
			"application/x-www-form-urlencoded" : "application/json";
		try {
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if(connection instanceof HttpsURLConnection https) {
				https.setSSLSocketFactory(new TLSSocketFactory());
			}
			try {
				connection.setConnectTimeout(currentTimeOut);
				connection.setReadTimeout(currentTimeOut);
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", contentType);
				connection.setRequestProperty("Content-Language", "en-US");
				connection.setRequestProperty("Connection", "close");
				connection.setRequestProperty("Accept-Encoding", "");
				String userAgent = getUserAgent();
				if(userAgent != null) {
					connection.setRequestProperty("User-Agent", userAgent);
				}
				if(authorization != null) {
					connection.setRequestProperty("Authorization", authorization.getAuthorization());
				}
				connection.setDoInput(true);
				connection.setDoOutput(doOutput);
				connection.setUseCaches(false);
				connection.connect();
				if(method != null && method.equals(POST)) {
					Writer out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
					BufferedWriter writer = new BufferedWriter(out);
					writer.write(params);
					writer.flush();
					writer.close();
				}
				if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					if(!url.getHost().equals(connection.getURL().getHost())) {
						message = handler.getRedirectedMessage();
					}
					else {
						Reader in = new InputStreamReader(connection.getInputStream());
						BufferedReader reader = new BufferedReader(in);
						String line;
						while((line = reader.readLine()) != null) {
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
			catch(SSLProtocolException |
				  EOFException |
				  SocketTimeoutException |
				  StreamResetException e) {
				e.printStackTrace();
				try {
					return retry(host, params, method);
				}
				catch(Exception ex) {
					throw new RuntimeException(ex);
				}
			}
			catch(SocketException se) {
				se.printStackTrace();
				String error = se.getMessage();
				if(error != null && error.contains("close")) {
					try {
						return retry(host, params, method);
					}
					catch(Exception e) {
						throw new RuntimeException(e);
					}
				}
				else {
					exception = se.toString();
					message = handler.getTimeOutMessage();
				}
			}
			catch(UnknownHostException he) {
				exception = he.toString();
				message = handler.getWeakInternetMessage();
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
				exception = ioe.toString();
				message = handler.getDefaultMessage();
			}
			catch(Exception e) {
				e.printStackTrace();
				exception = e.toString();
				message = e.getMessage();
			}
			finally {
				if(connection != null) {
					connection.disconnect();
				}
			}
		}
		catch(MalformedURLException mue) {
			mue.printStackTrace();
			exception = mue.toString();
			message = handler.getInvalidURLMessage();
		}
		catch(Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if(!result) {
			try {
				JSONObject field = new JSONObject();
				JSONObject error = new JSONObject();
				error.put("type", "android");
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response.append(error);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return response.toString();
	}


	public void downloadFile(
		String folder,
		String fileName,
		boolean external,
		Cipher cipher,
		OnDownloadFileCallback callback
	) {
		ErrorHandler handler = new ErrorHandler();
		try {
			String ex = context.getExternalFilesDir(null).getPath() + "/" + folder;
			File dir = external ? new File(ex) : context.getDir(folder, Context.MODE_PRIVATE);
			boolean result = dir.exists() || dir.mkdir();
			if(result) {
				URL url = new URL(this.url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				if(connection instanceof HttpsURLConnection https) {
					https.setSSLSocketFactory(new TLSSocketFactory());
				}
				String userAgent = getUserAgent();
				if(userAgent != null) {
					connection.setRequestProperty("User-Agent", userAgent);
				}
				connection.setConnectTimeout(currentTimeOut);
				connection.setReadTimeout(currentTimeOut);
				String path = dir.getPath() + "/" + fileName;
				File file = new File(path);
				int downloaded = 0;
				if(file.exists()) {
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
				if(cipher != null) {
					cos = new CipherOutputStream(output, cipher);
				}
				int count;
				int progress = downloaded;
				byte[] data = new byte[1024];
				while((count = input.read(data)) > 0) {
					progress += count;
					if(cipher != null) {
						cos.write(data, 0, count);
					}
					else {
						output.write(data, 0, count);
					}
					if(callback != null) {
						callback.onProgress(progress, max);
					}
				}
				if(cipher != null) {
					cos.flush();
					cos.close();
				}
				else {
					output.flush();
					output.close();
				}
				input.close();
				if(callback != null) {
					callback.onComplete();
				}
				connection.disconnect();
			}
			else {
				if(callback != null) {
					callback.onError(handler.getDirectoryMessage());
				}
			}
		}
		catch(SocketTimeoutException ste) {
			ste.printStackTrace();
			if(callback != null) {
				callback.onError(handler.getTimeOutMessage());
			}
		}
		catch(SSLException s) {
			s.printStackTrace();
			if(callback != null) {
				callback.onError(handler.getConnectionLostMessage());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			if(callback != null) {
				String error = e.getMessage();
				callback.onError(error);
			}
		}
	}

	public String uploadFile(
		JSONObject paramsObj,
		String name,
		File file
	) {
		String response = null;
		String message = null;
		String exception = null;
		boolean result = false;
		ErrorHandler handler = new ErrorHandler();
		try {
			Multipart multipart = new Multipart(url, "UTF-8");
			if(authorization != null) {
				multipart.addHeaderField("Authorization",
					authorization.getAuthorization());
				String userAgent = getUserAgent();
				if(userAgent != null) {
					multipart.addHeaderField("User-Agent", userAgent);
				}
			}
			try {
				Iterator<String> keys = paramsObj.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					String value = paramsObj.getString(key);
					multipart.addFormField(key, value);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(file != null && file.exists()) {
				multipart.addFilePart(name, file);
			}
			response = multipart.finish(handler);
			result = true;
		}
		catch(SocketTimeoutException ste) {
			ste.printStackTrace();
			exception = ste.toString();
			message = handler.getTimeOutMessage();
		}
		catch(UnknownHostException he) {
			exception = he.toString();
			message = handler.getWeakInternetMessage();
		}
		catch(IOException e) {
			exception = e.toString();
			message = handler.getDefaultMessage();
		}
		catch(Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if(!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}

	public String uploadFile(
		String params,
		String name,
		File file
	) {
		String response = null;
		String message = null;
		String exception = null;
		boolean result = false;
		ErrorHandler handler = new ErrorHandler();
		Console.logUrl(url);
		Console.logParams(params);
		try {
			Multipart multipart = new Multipart(url, "UTF-8");
			if(authorization != null) {
				multipart.addHeaderField("Authorization",
					authorization.getAuthorization());
			}
			String userAgent = getUserAgent();
			if(userAgent != null) {
				multipart.addHeaderField("User-Agent", userAgent);
			}
			multipart.addFormField("params", params);
			multipart.addFilePart(name, file);
			response = multipart.finish(handler);
			Console.logResponse(response);
			result = true;
		}
		catch(SocketTimeoutException ste) {
			ste.printStackTrace();
			exception = ste.toString();
			message = handler.getTimeOutMessage();
		}
		catch(UnknownHostException he) {
			exception = he.toString();
			message = handler.getWeakInternetMessage();
		}
		catch(IOException e) {
			exception = e.toString();
			message = handler.getDefaultMessage();
		}
		catch(Exception e) {
			e.printStackTrace();
			exception = e.toString();
			message = e.getMessage();
		}
		if(!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}
}
