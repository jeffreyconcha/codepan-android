package com.codepan.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.codepan.R;
import com.codepan.cache.TypefaceCache;
import com.codepan.database.SQLiteAdapter;
import com.codepan.location.Place;
import com.codepan.model.GpsData;
import com.codepan.model.MockData;
import com.codepan.model.PhoneInfoData;
import com.codepan.model.StampData;
import com.codepan.model.SystemMediaData;
import com.codepan.time.DateTime;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CustomTypefaceSpan;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class CodePanUtils {

	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		return String.format(Locale.ENGLISH, "%tF", cal);
	}

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		return String.format(Locale.ENGLISH, "%tT", cal);
	}

	public static String getDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tF", cal);
	}

	public static String getDate(TimeZone zone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(zone);
		return String.format(Locale.ENGLISH, "%tF", cal);
	}

	public static String getTime(TimeZone zone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(zone);
		return String.format(Locale.ENGLISH, "%tT", cal);
	}

	public static String getTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tT", cal);
	}

	public static String getUTCTime() {
		final String PATTERN = "yyyy-MM-dd HH:mm:ss";
		final SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format.format(new Date(System.currentTimeMillis()));
	}

	public static long dateToMillis(String date) {
		long millis = 0;
		if(date != null && !date.isEmpty()) {
			try {
				final String PATTERN = "yyyy-MM-dd";
				SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
				java.util.Date d = format.parse(date);
				if(d != null) {
					millis = d.getTime();
				}
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		return millis;
	}

	public static long timeToMillis(String time) {
		long millis = 0;
		if(time != null && !time.isEmpty()) {
			try {
				final String PATTERN = "HH:mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
				java.util.Date d = format.parse(time);
				if(d != null) {
					millis = d.getTime();
				}
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		return millis;
	}

	public static long dateTimeToMillis(String date, String time) {
		long millis = 0;
		if(date != null && time != null && !date.isEmpty() && !time.isEmpty()) {
			try {
				final String PATTERN = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
				java.util.Date d = format.parse(date + " " + time);
				if(d != null) {
					millis = d.getTime();
				}
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		return millis;
	}

	public static String formatDate(String date) {
		if(date != null && !date.isEmpty()) {
			String[] array = date.split("-");
			int m = Integer.parseInt(array[1]);
			int d = Integer.parseInt(array[2]);
			String month = String.format(Locale.ENGLISH, "%02d", m);
			String day = String.format(Locale.ENGLISH, "%02d", d);
			return array[0] + "-" + month + "-" + day;
		}
		else {
			return null;
		}
	}

	public static String formatTime(String time) {
		if(time != null && !time.isEmpty()) {
			String[] array = time.split(":");
			int h = Integer.parseInt(array[0]);
			int m = Integer.parseInt(array[1]);
			int s = Integer.parseInt(array[2]);
			String hour = String.format(Locale.ENGLISH, "%02d", h);
			String min = String.format(Locale.ENGLISH, "%02d", m);
			String sec = String.format(Locale.ENGLISH, "%02d", s);
			return hour + ":" + min + ":" + sec;
		}
		else {
			return null;
		}
	}

	public static String getReadableDate(String date, boolean isShort, boolean withYear) {
		return getReadableDate(date, isShort, withYear, false);
	}

	public static String getReadableDate(String date, boolean isShort,
		boolean withYear, boolean withDay) {
		String readable = "N/A";
		if(date != null && !date.isEmpty() && !date.equals("0000-00-00")) {
			long timestamp = dateToMillis(date);
			return getReadableDate(timestamp, isShort, withYear, withDay);
		}
		return readable;
	}

	public static String getReadableDate(long timestamp, boolean isShort,
		boolean withYear, boolean withDay) {
		String pattern = "EEE, MMMM d, yyyy";
		if(isShort) {
			pattern = pattern.replace("MMMM", "MMM");
		}
		if(!withYear) {
			pattern = pattern.replace(", yyyy", "");
		}
		if(!withDay) {
			pattern = pattern.replace("EEE, ", "");
		}
		final SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getDefault());
		return format.format(new Date(timestamp));
	}

	public static String getReadableTime(String time, boolean withSeconds) {
		String readable = "N/A";
		if(time != null && !time.isEmpty()) {
			long timestamp = timeToMillis(time);
			return getReadableTime(timestamp, withSeconds);
		}
		return readable;
	}

	public static String getReadableTime(long timestamp, boolean withSeconds) {
		String pattern = "h:mm:ss a";
		if(!withSeconds) {
			pattern = pattern.replace(":ss", "");
		}
		final SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return format.format(new Date(timestamp));
	}

	public static String dateTimeToHistory(String date, String time) {
		String history = null;
		if(date != null && time != null) {
			final long MIN = 60000L;
			final long HOUR = 3600000L;
			final long DAY = 86400000L;
			final long WEEK = 604800000L;
			final long MONTH = 2592000000L;
			long millis = dateTimeToMillis(date, time);
			long current = System.currentTimeMillis();
			if(current > millis) {
				long difference = current - millis;
				if(difference > MONTH) {
					String d = getReadableDate(date, true, true);
					String t = getReadableTime(time, false);
					history = d + " at " + t;
				}
				else if(difference >= WEEK) {
					int w = (int) (difference / WEEK);
					history = w > 1 ? (w + " weeks ago") : (w + " week ago");
				}
				else if(difference >= DAY) {
					int d = (int) (difference / DAY);
					history = d > 1 ? (d + " days ago") : (d + " day ago");
				}
				else if(difference >= HOUR) {
					int h = (int) (difference / HOUR);
					history = h > 1 ? (h + " hours ago") : (h + " hour ago");
				}
				else if(difference >= MIN) {
					int m = (int) (difference / MIN);
					history = m > 1 ? (m + " mins ago") : (m + " min ago");
				}
				else {
					history = "Just now";
				}
			}
			else {
				history = "Just now";
			}
		}
		return history;
	}

	public static String millisToTime(long millis, boolean isWord, boolean hasSecond) {
		String result = "";
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(millis);
		String date = String.format(Locale.ENGLISH, "%tF", cal);
		String time = String.format(Locale.ENGLISH, "%tT", cal);
		String[] dateArray = date.split("-");
		String[] timeArray = time.split(":");
		int y = Integer.parseInt(dateArray[0]) - 1970;
		int M = Integer.parseInt(dateArray[1]) - 1;
		int d = Integer.parseInt(dateArray[2]) - 1;
		int h = Integer.parseInt(timeArray[0]);
		int m = Integer.parseInt(timeArray[1]);
		int s = Integer.parseInt(timeArray[2]);
		if(!isWord) {
			String year = y > 0 ? (y + "y ") : "";
			String month = M > 0 ? (M + "mo ") : "";
			String day = d > 0 ? (d + "d ") : "";
			String hour = h > 0 ? (h + "h ") : "";
			String min = m > 0 ? (m + "m ") : "";
			String sec = s > 0 ? (s + "s") : "0s";
			result = year + month + day + hour + min;
			if(hasSecond || result.isEmpty()) {
				result += sec;
			}
		}
		else {
			String year = y > 0 ? (y + (y > 1 ? " years " : " year ")) : "";
			String month = M > 0 ? (M + (M > 1 ? " months " : " month ")) : "";
			String day = d > 0 ? (d + (d > 1 ? " days" : " day ")) : "";
			String hour = h > 0 ? (h + (h > 1 ? " hrs " : " hr ")) : "";
			String min = m > 0 ? (m + (m > 1 ? " mins " : " min ")) : "";
			String sec = s > 0 ? (s + (s > 1 ? " secs" : " sec")) : "";
			result = year + month + day + hour + min;
			if(hasSecond || result.isEmpty()) {
				result += sec;
			}
		}
		return result;
	}

	public static String getDisplayYear(String date) {
		if(date != null) {
			return date.split("-")[0];
		}
		return null;
	}

	public static String rollDate(String date, int noOfDays) {
		if(date != null) {
			long millis = dateTimeToMillis(date, "00:00:00");
			long days = noOfDays * 86400000L;
			long output = millis + days;
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(output);
			return String.format(Locale.ENGLISH, "%tF", cal);
		}
		else {
			return null;
		}
	}

	public static Calendar getCalendar(String date) {
		Calendar cal = Calendar.getInstance();
		if(date != null) {
			long timestamp = dateToMillis(date);
			cal.setTimeInMillis(timestamp);
		}
		return cal;
	}

	public static void closePane(Context context, final View view, int id) {
		Animation anim = AnimationUtils.loadAnimation(context, id);
		view.setAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		view.startAnimation(anim);
	}

	public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	public static String unicodeToString(String input) {
		String utf8 = "";
		if(input != null && !input.isEmpty()) {
			try {
				String text = cleanUnicodeString(input);
				byte[] bytes = text.getBytes("UTF-8");
				utf8 = new String(bytes, Charset.forName("UTF-8"));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return utf8;
	}

	public static String cleanUnicodeString(String input) {
		final int SPACE = 32;
		StringBuilder builder = new StringBuilder();
		Pattern pattern = Pattern.compile("[u][0-9a-fA-F]{4}");
		Matcher matcher = pattern.matcher(input);
		int index = 0;
		while(matcher.find()) {
			String point = matcher.group().substring(1);
			int parsed = Integer.parseInt(point, 16);
			Character symbol = (char) parsed;
			String text = input.substring(index, matcher.start());
			builder.append(text);
			if(parsed >= SPACE) {
				builder.append(symbol);
			}
			else {
				builder.append(matcher.group());
			}
			index = matcher.end();
		}
		builder.append(input.substring(index));
		return builder.toString();
	}

	@SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException {
		FileChannel in = new FileInputStream(src).getChannel();
		FileChannel out = new FileOutputStream(dst).getChannel();
		try {
			in.transferTo(0, in.size(), out);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			in.close();
			out.close();
		}
	}

	public static String createParsableString(ArrayList<String> list, String delimeter) {
		String parsableString = "";
		int position = 0;
		for(String s : list) {
			if(position == list.size() - 1) {
				parsableString = parsableString + s;
			}
			else {
				parsableString = parsableString + s + delimeter;
			}
			position++;
		}
		return parsableString;
	}

	public static boolean decryptTextFile(Context context, String folder, String password) {
		boolean result = false;
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath();
		File dir = new File(path);
		if(dir.exists() && dir.isDirectory()) {
			String[] child = dir.list();
			if(child.length > 0) {
				for(String file : child) {
					result = file.contains(".") || decryptFile(context,
						folder, file, password, ".txt");
				}
			}
			else {
				result = true;
			}
		}
		else {
			result = true;
		}
		return result;
	}

	public static boolean decryptFile(Context context, String folderName, String fileName, String password, String extFile) {
		boolean result = false;
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		if(fileName.contains(extFile)) {
			return true;
		}
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(path + extFile);
			byte[] key = generateKey(password, 16);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			int b = 0;
			byte[] d = new byte[8];
			while((b = cis.read(d)) != -1) {
				fos.write(d, 0, b);
			}
			fos.flush();
			fos.close();
			cis.close();
			result = file.delete();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean deleteFile(String path) {
		File file = new File(path);
		return !(file.exists() && !file.isDirectory()) || file.delete();
	}

	public static boolean deleteFile(Context context, String folder, String fileName) {
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		return deleteFile(path);
	}

	public static void deleteFiles(String path) {
		File file = new File(path);
		if(file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean deleteFilesInDir(Context context, String folder) {
		boolean result = false;
		File dir = context.getDir(folder, Context.MODE_PRIVATE);
		if(dir.exists() && dir.isDirectory()) {
			String[] child = dir.list();
			if(child != null && child.length > 0) {
				for(String file : child) {
					result = new File(dir, file).delete();
				}
			}
			else {
				result = true;
			}
		}
		else {
			result = true;
		}
		return result;
	}

	public static boolean downloadFile(Context context, String urlLink, String folder,
		String fileName) {
		boolean result = false;
		File dir = context.getDir(folder, Context.MODE_PRIVATE);
		if(!dir.exists()) {
			dir.mkdir();
		}
		try {
			URL url = new URL(urlLink);
			URLConnection connection = url.openConnection();
			connection.connect();
			//int fileLength = connection.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(dir + "/" + fileName);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String encryptString(String text, String password) {
		String encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final byte[] ivData = new byte[cipher.getBlockSize()];
			final SecureRandom sr = new SecureRandom();
			sr.nextBytes(ivData);
			IvParameterSpec iv = new IvParameterSpec(ivData);
			byte[] key = generateKey(password, 32);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			byte[] inputByte = text.getBytes("UTF-8");
			cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
			encrypted = new String(Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}

	public static boolean encryptFile(Context context, String folderName, String fileName, String password, String extFile) {
		boolean result = false;
		String pathIn = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		String pathOut = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName.replace(extFile, "");
		try {
			File file = new File(pathIn);
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(pathOut);
			byte[] key = generateKey(password, 16);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sks);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			int b;
			byte[] d = new byte[8];
			while((b = fis.read(d)) != -1) {
				cos.write(d, 0, b);
			}
			cos.flush();
			cos.close();
			fis.close();
			result = file.delete();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void expandView(final View view, final boolean isVertical) {
		view.setVisibility(View.VISIBLE);
		view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int value = isVertical ? view.getMeasuredHeight() : view.getMeasuredWidth();
		ValueAnimator animator = ValueAnimator.ofInt(0, value);
		animator.setDuration(250);
		animator.addUpdateListener(valueAnimator -> {
			int value1 = (Integer) valueAnimator.getAnimatedValue();
			LayoutParams layoutParams = view.getLayoutParams();
			if(isVertical) {
				layoutParams.height = value1;
			}
			else {
				layoutParams.width = value1;
			}
			view.setLayoutParams(layoutParams);
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if(isVertical) {
					view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				}
				else {
					view.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}
		});
		animator.start();
	}

	public static void collapseView(final View view, final boolean isVertical) {
		int value = isVertical ? view.getMeasuredHeight() : view.getMeasuredWidth();
		ValueAnimator animator = ValueAnimator.ofInt(value, 0);
		animator.setDuration(250);
		animator.addUpdateListener(valueAnimator -> {
			int value1 = (Integer) valueAnimator.getAnimatedValue();
			LayoutParams layoutParams = view.getLayoutParams();
			if(isVertical) {
				layoutParams.height = value1;
			}
			else {
				layoutParams.width = value1;
			}
			view.setLayoutParams(layoutParams);
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				view.setVisibility(View.GONE);
				if(isVertical) {
					view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				}
				else {
					view.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}
		});
		animator.start();
	}

	public static void slideView(final View view, final boolean up, final long delay) {
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
			view.getLayoutParams();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(up) {
					params.bottomMargin += 10;
					view.setLayoutParams(params);
					if(params.bottomMargin < 0) {
						handler.postDelayed(this, delay);
					}
					else {
						params.bottomMargin = 0;
						view.setLayoutParams(params);
					}
				}
				else {
					int size = -view.getHeight();
					params.bottomMargin -= 10;
					view.setLayoutParams(params);
					if(params.bottomMargin > size) {
						handler.postDelayed(this, delay);
					}
					else {
						params.bottomMargin = size;
						view.setLayoutParams(params);
					}
				}
			}
		}, delay);
	}

	@SuppressWarnings("resource")
	public static boolean extractDatabase(Context context, String folder, String name, boolean external) {
		boolean result = false;
		File dir = null;
		try {
			if(external) {
				String path = context.getExternalFilesDir(null).getPath() + "/" + folder;
				dir = new File(path);
			}
			else {
				dir = context.getDir(folder, Context.MODE_PRIVATE);
			}
			if(!dir.exists()) {
				dir.mkdir();
			}
			if(dir.canWrite()) {
				String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + name;
				File data = Environment.getDataDirectory();
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(dir, name);
				if(currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static byte[] generateKey(String password, int length) {
		byte[] key = null;
		try {
			key = password.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, length);
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static int getBackStackCount(FragmentActivity activity) {
		return activity.getSupportFragmentManager().getBackStackEntryCount();
	}

	public static int getBatteryLevel(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.getApplicationContext().registerReceiver(null, filter);
		if(intent != null) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			return (level * 100) / scale;
		}
		else {
			return 0;
		}
	}

	public static Bitmap getBitmapImage(Context context, String folderName, String fileName) {
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		return getBitmapImage(path);
	}

	public static Bitmap getBitmapImage(String path) {
		File image = new File(path);
		return BitmapFactory.decodeFile(image.getAbsolutePath());
	}

	@SuppressLint("HardwareIds")
	@RequiresPermission("android.permission.READ_PHONE_STATE")
	public static String getDeviceId(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager != null) {
			if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
				return manager.getDeviceId();
			}
			else {
				return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			}
		}
		return null;
	}

	@SuppressLint("MissingPermission")
	public static PhoneInfoData getPhoneInfo(Context context) {
		PhoneInfoData info = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager != null) {
			if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
				String operator = manager.getNetworkOperator();
				info = new PhoneInfoData();
				info.mcc = Integer.parseInt(operator.substring(0, 3));
				info.mnc = Integer.parseInt(operator.substring(3));
				final GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
				if(location != null) {
					info.cid = location.getCid();
					info.lac = location.getLac();
				}
			}
		}
		return info;
	}

	@SuppressLint({"MissingPermission", "HardwareIds"})
	public static String getPhoneNumber(Context context) {
		String mobileNo = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager != null) {
			String number = manager.getLine1Number();
			if(number != null && !number.trim().isEmpty()) {
				mobileNo = number;
			}
		}
		return mobileNo;
	}

	public static String getImagePath(Context context, Uri uri) {
		String path = null;
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if(cursor != null) {
			cursor.moveToFirst();
			String documentID = cursor.getString(0);
			documentID = documentID.substring(documentID.lastIndexOf(":") + 1);
			cursor.close();
			cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
				null, Media._ID + " = ? ", new String[]{documentID}, null);
			if(cursor != null) {
				cursor.moveToFirst();
				path = cursor.getString(cursor.getColumnIndex(Media.DATA));
				cursor.close();
			}
		}
		return path;
	}

	public static String getKeyHash(Context context) {
		String keyHash = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for(Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return keyHash;
	}

	public static int getLastRecordID(SQLiteAdapter db, String table) {
		int lastRecordID = 0;
		String query = "SELECT ID FROM " + table + " ORDER BY ID DESC LIMIT 1";
		lastRecordID = db.getInt(query);
		return lastRecordID;
	}

	public static String getMySQLPassword(String plainText) {
		String password = "";
		try {
			byte[] utf8 = plainText.getBytes("UTF-8");
			password = "*" + DigestUtils.shaHex(DigestUtils.sha(utf8)).toUpperCase();
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return password;
	}

	public static String getNameOfMonths(int month, boolean isShort, boolean isUpperCase) {
		String nameOfMonths = "";
		switch(month) {
			case 1:
				nameOfMonths = "January";
				break;
			case 2:
				nameOfMonths = "February";
				break;
			case 3:
				nameOfMonths = "March";
				break;
			case 4:
				nameOfMonths = "April";
				break;
			case 5:
				nameOfMonths = "May";
				break;
			case 6:
				nameOfMonths = "June";
				break;
			case 7:
				nameOfMonths = "July";
				break;
			case 8:
				nameOfMonths = "August";
				break;
			case 9:
				nameOfMonths = "September";
				break;
			case 10:
				nameOfMonths = "October";
				break;
			case 11:
				nameOfMonths = "November";
				break;
			case 12:
				nameOfMonths = "December";
				break;
		}
		if(!nameOfMonths.isEmpty()) {
			if(isShort) {
				nameOfMonths = nameOfMonths.substring(0, 3);
			}
			if(isUpperCase) {
				String upperCase = "";
				for(int x = 0; x < nameOfMonths.length(); x++) {
					Character temp;
					temp = nameOfMonths.charAt(x);
					temp = Character.toUpperCase(temp);
					upperCase = upperCase + temp.toString();
				}
				nameOfMonths = upperCase;
			}
		}
		return nameOfMonths;
	}

	public static String getDay(String date) {
		long timestamp = dateTimeToMillis(date, "00:00:00");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tA", cal);
	}

	public static String getVersionName(Context context) {
		String name = null;
		final PackageManager pm = context.getPackageManager();
		if(pm != null) {
			try {
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				name = pi.versionName;
			}
			catch(NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	public static String getDeviceModel() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if(model != null && manufacturer != null) {
			if(model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
				return model;
			}
			else {
				return capitalizeWord(manufacturer) + " " + model;
			}
		}
		return null;
	}

	public static String nullify(String text) {
		if(text == null || text.equals("null")) {
			return null;
		}
		return text;
	}

	public static String handleQuotesUniCodeToSQLite(String text) {
		String result = "";
		if(text != null && !text.equals("null")) {
			result = text
				.replace("u0027", "''")
				.replace("u0022", "\"");
		}
		return result;
	}

	public static void showKeyboard(View v, Context context) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.showSoftInput(v, 0);
	}

	public static void showKeyboard(View v, Activity activity) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.showSoftInput(v, 0);
	}

	public static void hideKeyboard(View v, Activity activity) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void hideKeyboard(View v, Context context) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static boolean isGpsEnabled(Context context) {
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@SuppressLint("MissingPermission")
	public static void turnOnWifi(Context context) {
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
			WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			if(wm != null) {
				wm.setWifiEnabled(true);
			}
		}
	}

	@SuppressLint("MissingPermission")
	public static void turnOffWifi(Context context) {
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
			WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			if(wm != null) {
				wm.setWifiEnabled(false);
			}
		}
	}

	@SuppressLint("MissingPermission")
	public static boolean hasInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm != null) {
			NetworkInfo network = cm.getActiveNetworkInfo();
			return network != null && network.isAvailable();
		}
		return false;
	}

	public static boolean isMockEnabled(Context context) {
		boolean result = false;
		try {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
				if(manager != null) {
					String name = context.getPackageName();
					int op = manager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION,
						android.os.Process.myUid(), name);
					result = op == AppOpsManager.MODE_ALLOWED;
				}
			}
			else {
				result = !Secure.getString(context.getContentResolver(),
					"mock_location").equals("0");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static MockData getMockLocationApp(Context context, String[] exceptions) {
		PackageManager pm = context.getPackageManager();
		List<String> appList = exceptions != null ? Arrays.asList(exceptions) : new ArrayList<>();
		List<ApplicationInfo> packageList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for(ApplicationInfo info : packageList) {
			int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
			if((info.flags & mask) == 0) {
				try {
					PackageInfo pi = pm.getPackageInfo(info.packageName, PackageManager.GET_PERMISSIONS);
					String[] permissionList = pi.requestedPermissions;
					if(permissionList != null) {
						for(String permission : permissionList) {
							if(permission.equals("android.permission.ACCESS_MOCK_LOCATION")
								&& !info.packageName.equals(context.getPackageName())
								&& info.enabled && !appList.contains(info.packageName)) {
								MockData mock = new MockData();
								mock.packageId = info.packageName;
								mock.label = pm.getApplicationLabel(info).toString();
								return mock;
							}
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static boolean isNetEnabled(Context context) {
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isNumeric(String text) {
		if(text == null || text.isEmpty() || text.equals(".")) {
			return false;
		}
		String t = text.replace(",", "");
		if(t.length() > 1) {
			while(t.indexOf('0') == 0) {
				t = t.substring(1);
			}
		}
		return NumberUtils.isNumber(t);
	}

	public static int parseInt(String input) {
		if(isNumeric(input)) {
			final String clean = input.trim().replace(",", "");
			return Integer.parseInt(clean);
		}
		return 0;
	}

	public static float parseFloat(String input) {
		if(isNumeric(input)) {
			final String clean = input.trim().replace(",", "");
			return Float.parseFloat(clean);
		}
		return 0F;
	}

	public static double parseDouble(String input) {
		if(isNumeric(input)) {
			final String clean = input.trim().replace(",", "");
			return Double.parseDouble(clean);
		}
		return 0D;
	}

	public static boolean isServiceRunning(Context context, Class<?> c) {
		boolean result = false;
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if(c.getName().equals(service.service.getClassName())) {
				result = true;
			}
		}
		return result;
	}

	public static boolean isSubsetOf
		(Collection<String> subset, Collection<String> superset) {
		for(String string : subset) {
			if(!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isTimeEqual(int allowanceMin, String dateToCompare, String
		timeToCompare,
		String baseDate, String baseTime) {
		boolean result = false;
		long millisToCompare = dateTimeToMillis(dateToCompare, timeToCompare);
		long millisBase = dateTimeToMillis(baseDate, baseTime);
		long millisAllowance = allowanceMin * 60000;
		long difference = millisToCompare > millisBase ? millisToCompare - millisBase : millisBase - millisToCompare;
		if(difference <= millisAllowance) {
			result = true;
		}
		return result;
	}

	public static boolean isValidEmail(String email) {
		return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static boolean isValidURL(String url) {
		return URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches();
	}

	public static void removeNotification(Context context, int notificationID) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if(manager != null) manager.cancel(notificationID);
	}

	public static boolean alarmExists(Context context, Class<?> receiver, String action, int requestCode) {
		Intent intent = new Intent(context, receiver);
		intent.setAction(action);
		int flags = PendingIntent.FLAG_NO_CREATE;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		return pi != null;
	}

	public static boolean alarmExists(Context context, Intent intent, int requestCode) {
		int flags = PendingIntent.FLAG_NO_CREATE;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		return pi != null;
	}

	public static void cancelAlarm(Context context, Class<?> receiver, String action, int requestCode) {
		Intent intent = new Intent(context, receiver);
		intent.setAction(action);
		int flags = 0;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		if(pi != null) {
			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if(manager != null) {
				manager.cancel(pi);
			}
			pi.cancel();
		}
	}

	public static void cancelAlarm(Context context, Class<?> receiver, int requestCode) {
		Intent intent = new Intent(context, receiver);
		int flags = 0;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		if(pi != null) {
			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if(manager != null) {
				manager.cancel(pi);
			}
			pi.cancel();
		}
	}

	@RequiresPermission(value = "android.permission.SCHEDULE_EXACT_ALARM", conditional = true)
	public static void setAlarm(Context context, PendingIntent pi, long trigger, int type) {
		if(trigger > 0) {
			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if(manager != null) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						if(manager.canScheduleExactAlarms()) {
							manager.setExactAndAllowWhileIdle(type, trigger, pi);
						}
						else {
							Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
							context.startActivity(intent);
						}
					}
					else {
						manager.setExactAndAllowWhileIdle(type, trigger, pi);
					}
				}
				else {
					manager.setExact(type, trigger, pi);
				}
			}
		}
	}

	public static void setAlarm(Context context, Intent intent, int minutes, int requestCode) {
		if(minutes > 0) {
			int flags = PendingIntent.FLAG_UPDATE_CURRENT;
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
			}
			PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
			int seconds = minutes * 60;
			long trigger = System.currentTimeMillis() + (seconds * 1000L);
			setAlarm(context, pi, trigger, AlarmManager.RTC_WAKEUP);
		}
	}

	public static void setAlarm(Context context, Class<?> receiver, int minutes,
		int requestCode) {
		if(minutes > 0) {
			Intent intent = new Intent(context, receiver);
			int flags = PendingIntent.FLAG_UPDATE_CURRENT;
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
			}
			PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
			int seconds = minutes * 60;
			long trigger = System.currentTimeMillis() + (seconds * 1000L);
			setAlarm(context, pi, trigger, AlarmManager.RTC_WAKEUP);
		}
	}

	public static void setAlarm(Context context, Intent intent, long schedule, int requestCode) {
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		setAlarm(context, pi, schedule, AlarmManager.RTC_WAKEUP);
	}

	public static void setAlarm(Context context, Class<?> receiver, long schedule,
		int requestCode) {
		Intent intent = new Intent(context, receiver);
		setAlarm(context, intent, schedule, requestCode);
	}

	public static void setCircle(ImageView view) {
		Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap circleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);
		Canvas c = new Canvas(circleBitmap);
		float cx = (float) width / 2F;
		float cy = (float) height / 2F;
		float radius = (float) width / 2F;
		c.drawCircle(cx, cy, radius, paint);
		view.setImageBitmap(circleBitmap);
	}

	public static boolean setErrorMsg(Context context, String message, String folder, String
		password) {
		boolean result = false;
		String errorMsg = "Error: " + message;
		errorMsg = errorMsg.replace("\n", "\r\n");
		String fileName = getDate() + "_" + getTime() + ".txt";
		fileName = fileName.replace(":", "-");
		result = writeText(context, folder, fileName, errorMsg);
		if(result) {
			result = encryptFile(context, folder, fileName, password, ".txt");
		}
		return result;
	}

	public static boolean setErrorMsg(Context context, String message, String url, String
		jsonString,
		String response, String folder, String password) {
		boolean result = false;
		String errorMsg = "Error: " + message + "\nURL: " + url + "\nParams: " +
			jsonString + "\nResponse: " + response;
		errorMsg = errorMsg.replace("\n", "\r\n");
		String fileName = getDate() + "_" + getTime() + ".txt";
		fileName = fileName.replace(":", "-");
		result = writeText(context, folder, fileName, errorMsg);
		if(result) {
			result = encryptFile(context, folder, fileName, password, ".txt");
		}
		return result;
	}

	public static void setGPS(Context context, boolean isEnabled) {
		try {
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", isEnabled);
			context.sendBroadcast(intent);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static NotificationCompat.Builder createNotificationBuilder(Context context,
		String channelID, String channelName, int icon) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			if(manager != null) {
				NotificationChannel channel = new NotificationChannel(channelID, channelName,
					NotificationManager.IMPORTANCE_DEFAULT);
				manager.createNotificationChannel(channel);
			}
		}
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
		builder.setPriority(NotificationCompat.PRIORITY_LOW);
		builder.setSmallIcon(icon);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		return builder;
	}

	public static void setNotification(Context context, int notificationID, Notification
		notification) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if(manager != null) {
			manager.notify(notificationID, notification);
		}
	}

	public static void setNotification(Context context, String channelID, String channelName,
		int notificationID, NotificationCompat.Builder builder) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if(manager != null) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				NotificationChannel channel = new NotificationChannel(channelID, channelName,
					NotificationManager.IMPORTANCE_DEFAULT);
				manager.createNotificationChannel(channel);
			}
			manager.notify(notificationID, builder.build());
		}
	}

	public static void setNotification(Context context, String title, String message,
		String channelID, String channelName, int icon, int notificationID, int requestCode,
		int color, boolean isVibrate, Intent intent, String uri) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
		builder.setSmallIcon(icon);
		builder.setContentTitle(title);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setLights(Color.GREEN, 500, 500);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		builder.setColor(color);
		if(isVibrate) {
			builder.setVibrate(new long[]{500, 500});
		}
		Uri url = Uri.parse(uri);
		builder.setSound(url);
		builder.setContentText(message);
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pi = PendingIntent.getActivity(context, requestCode,
			intent, flags);
		builder.setContentIntent(pi);
		setNotification(context, channelID, channelName, notificationID, builder);
	}

	public static void setNotification(Context context, String title, String message,
		String channelID, String channelName, int resource, int notificationID,
		boolean isVibrate, String uri) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
		builder.setSmallIcon(resource);
		builder.setContentTitle(title);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setLights(Color.GREEN, 500, 500);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		if(isVibrate) {
			builder.setVibrate(new long[]{500, 500});
		}
		Uri url = Uri.parse(uri);
		builder.setSound(url);
		builder.setContentText(message);
		setNotification(context, channelID, channelName, notificationID, builder);
	}

	public static String throwableToString(Throwable th) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}

	public static boolean writeText(Context context, String folderName, String fileName, String
		text) {
		boolean result = false;
		try {
			String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
			File file = new File(path);
			FileWriter writer = new FileWriter(file);
			writer.append(text);
			writer.flush();
			writer.close();
			result = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean zipFile(Context context, String fileName, String folderName, String
		zipFileName) {
		String pathToZip = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		String pathForZip = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + zipFileName;
		int BUFFER = 80000;
		boolean result = false;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(pathForZip);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte[] data = new byte[BUFFER];
			FileInputStream fi = new FileInputStream(pathToZip);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(pathToZip.substring(pathToZip.lastIndexOf("/") + 1));
			out.putNextEntry(entry);
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
			out.close();
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean zipFolder(Context context, String folderToZip, String
		folderForZip, String zipFileName) {
		String pathToZip = context.getDir(folderToZip, Context.MODE_PRIVATE).getPath();
		String pathForZip = context.getDir(folderForZip, Context.MODE_PRIVATE).getPath() + "/" + zipFileName;
		boolean result = false;
		try {
			FileOutputStream fos = new FileOutputStream(pathForZip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File srcFile = new File(pathToZip);
			File[] files = srcFile.listFiles();
			for(File file : files) {
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(file);
				zos.putNextEntry(new ZipEntry(file.getName()));
				int length;
				while((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			result = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void alertToast(FragmentActivity activity, String message, int duration) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(message);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public static void alertToast(FragmentActivity activity, String message) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(message);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static void alertToast(FragmentActivity activity, SpannableStringBuilder ssb) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(ssb);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static void alertToast(FragmentActivity activity, SpannableStringBuilder ssb,
		int duration) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(ssb);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public static void alertToast(FragmentActivity activity, int res) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(res);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static void alertToast(FragmentActivity activity, String message,
		int duration, ArrayList<SpannableMap> list, Typeface typeface) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = layout.findViewById(R.id.tvMessageAlertToast);
		SpannableStringBuilder ssb = new SpannableStringBuilder(message);
		for(SpannableMap obj : list) {
			ssb.setSpan(new CustomTypefaceSpan(typeface), obj.start, obj.end, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}
		text.setText(ssb);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public static void triggerHeartbeat(Context context) {
		context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
		context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
	}

	public static String convertBengaliNumerals(String text) {
		return text.replace("০", "0")
			.replace("১", "1")
			.replace("২", "2")
			.replace("৩", "3")
			.replace("৪", "4")
			.replace("৫", "5")
			.replace("৬", "6")
			.replace("৭", "7")
			.replace("৮", "8")
			.replace("৯", "9");
	}

	public static boolean saveBitmap(
		Context context,
		String folder,
		String fileName,
		Bitmap bitmap
	) {
		boolean result = false;
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(out != null) {
					out.close();
					result = true;
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean saveBitmap(
		Context context,
		String folder,
		String fileName,
		Bitmap bitmap,
		long maxSize
	) {
		boolean result = false;
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		FileOutputStream out = null;
		try {
			int quality = 100;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
			stream.flush();
			int size = stream.size();
			Console.debug("INITIAL SIZE: " + size);
			while(size > maxSize) {
				stream.reset();
				quality -= 5;
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
				stream.flush();
				size = stream.size();
				Console.debug(fileName + " SIZE: " + size);
			}
			out = new FileOutputStream(path);
			out.write(stream.toByteArray());
			out.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(out != null) {
					out.close();
					result = true;
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean isThreadRunning(String name) {
		Set<Thread> i = Thread.getAllStackTraces().keySet();
		for(Thread bg : i) {
			if(bg != null && bg.getName().equals(name) &&
				!bg.isInterrupted()) {
				return true;
			}
		}
		return false;
	}

	public static Thread getThread(String name) {
		Set<Thread> i = Thread.getAllStackTraces().keySet();
		for(Thread bg : i) {
			if(bg != null && bg.getName().equals(name) &&
				!bg.isInterrupted()) {
				return bg;
			}
		}
		return null;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float scaleWidth = ((float) width) / w;
		float scaleHeight = ((float) height) / h;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
	}

	public static Bitmap getBitmapThumbnails(Context context, String folderName, String
		fileName, int size) {
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		File image = new File(path);
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
			return null;
		}
		int originalSize = Math.max(bounds.outHeight, bounds.outWidth);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / size;
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

	public static int getSupportedNoOfCol(Context context) {
		final int numCol = 3;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float widthDp = metrics.widthPixels / metrics.density;
		if(numCol != 0) {
			if(widthDp <= 360) {
				if(metrics.widthPixels % numCol == 0) {
					return numCol;
				}
				else {
					return numCol - 1;
				}
			}
			else {
				int x = numCol + 1;
				while(metrics.widthPixels % x != 0) {
					x++;
				}
				return x;
			}
		}
		else {
			return numCol;
		}
	}

	public static boolean isTablet(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float widthDp = metrics.widthPixels / metrics.density;
		return widthDp >= 600;
	}

	public static int getMaxWidth(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	public static int getMaxHeight(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	public static int getSpacing(Context context, int numCol) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (numCol * (int) metrics.density);
	}

	public static int getWidth(View view) {
		int width = 0;
		if(view != null) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			width = view.getMeasuredWidth();
		}
		return width;
	}

	public static int getHeight(View view) {
		int height = 0;
		if(view != null) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			height = view.getMeasuredHeight();
		}
		return height;
	}

	public static void animateView(Context context, final View view, final int resID,
		final int filler) {
		Animation anim = AnimationUtils.loadAnimation(context, resID);
		anim.setFillAfter(true);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if(view instanceof ImageView) {
					((ImageView) view).setImageResource(filler);
				}
				else {
					view.setBackgroundResource(filler);
				}
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	public static void fadeIn(final View view) {
		view.setVisibility(View.VISIBLE);
		Animation fadeIn = new AlphaAnimation(0.00f, 1.00f);
		fadeIn.setDuration(250);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setEnabled(true);
			}
		});
		view.startAnimation(fadeIn);
	}

	public static void fadeIn(final View view, long duration) {
		view.setVisibility(View.VISIBLE);
		Animation fadeIn = new AlphaAnimation(0.00f, 1.00f);
		fadeIn.setDuration(duration);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setEnabled(true);
			}
		});
		view.startAnimation(fadeIn);
	}

	public static void fadeOut(final View view) {
		view.setEnabled(false);
		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
		fadeOut.setDuration(250);
		fadeOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(fadeOut);
	}

	public static void fadeOut(final View view, long duration) {
		view.setEnabled(false);
		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
		fadeOut.setDuration(duration);
		fadeOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(fadeOut);
	}

	public static String handleSpecialCharacters(String text) {
		if(text != null && !text.equals("null")) {
			if(containsSpecialCharacters(text)) {
				String replaced = text.replace("&NewLine;", "\n").
					replace("&Tab;", "    ");
				String unicode = unicodeToString(replaced);
				return StringEscapeUtils.unescapeHtml4(unicode);
			}
			return text;
		}
		return "";
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		}
		catch(NameNotFoundException e) {
			return false;
		}
	}

	public static int getWindowHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static int getWindowWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	public static void setStatusBarColor(Activity activity, int resID) {
		if(resID != 0) {
			int color = activity.getResources().getColor(resID);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(color);
			}
		}
	}

	public static SpannableStringBuilder customizeText(SpannableMap span, String text) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(text);
		switch(span.type) {
			case SpannableMap.COLOR:
				ssb.setSpan(new ForegroundColorSpan(span.color), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
			case SpannableMap.FONT:
				ssb.setSpan(new CustomTypefaceSpan(span.typeface), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
			case SpannableMap.IMAGE:
				ssb.setSpan(new ImageSpan(span.context, span.bitmap), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
		}
		return ssb;
	}

	public static SpannableStringBuilder customizeText(SpannableMap span) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		switch(span.type) {
			case SpannableMap.COLOR:
				ssb.setSpan(new ForegroundColorSpan(span.color), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
			case SpannableMap.FONT:
				ssb.setSpan(new CustomTypefaceSpan(span.typeface), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
			case SpannableMap.IMAGE:
				ssb.setSpan(new ImageSpan(span.context, span.bitmap), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
				break;
		}
		return ssb;
	}

	public static SpannableStringBuilder customizeText(ArrayList<SpannableMap> list, String
		text) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(text);
		for(SpannableMap span : list) {
			switch(span.type) {
				case SpannableMap.COLOR:
					ssb.setSpan(new ForegroundColorSpan(span.color), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
					break;
				case SpannableMap.FONT:
					ssb.setSpan(new CustomTypefaceSpan(span.typeface), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
					break;
				case SpannableMap.UNDERLINED:
					ssb.setSpan(new UnderlineSpan(), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
					break;
				case SpannableMap.ITALIC:
					ssb.setSpan(new StyleSpan(Typeface.ITALIC), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
					break;
				case SpannableMap.IMAGE:
					ssb.setSpan(new ImageSpan(span.context, span.bitmap), span.start, span.end, SPAN_INCLUSIVE_INCLUSIVE);
					break;
			}
		}
		return ssb;
	}

	public static SpannableStringBuilder customizeText(Context context, String text,
		SpannableMap.FontStyle style, char c) {
		if(text != null) {
			int index = 0;
			int start = 0;
			String replace = text.replace(String.valueOf(c), "");
			int length = replace.length();
			ArrayList<SpannableMap> map = new ArrayList<>();
			for(int i = 0; i < text.length(); i++) {
				if(text.charAt(i) == c) {
					if(index % 2 != 0) {
						int adj = map.size() * 2;
						int end = i - adj - 1;
						start -= adj;
						end = Math.min(end, length);
						map.add(new SpannableMap(context, style, start, end));
					}
					else {
						start = i;
					}
					index++;
				}
			}
			return customizeText(map, replace);
		}
		return null;
	}

	public static SpannableStringBuilder customizeText(Context context, String text,
		String font, char c) {
		if(text != null) {
			int index = 0;
			int start = 0;
			String replace = text.replace(String.valueOf(c), "");
			int length = replace.length();
			ArrayList<SpannableMap> map = new ArrayList<>();
			for(int i = 0; i < text.length(); i++) {
				if(text.charAt(i) == c) {
					if(index % 2 != 0) {
						int adj = map.size() * 2;
						int end = i - adj - 1;
						start -= adj;
						end = Math.min(end, length);
						map.add(new SpannableMap(context, font, start, end));
					}
					else {
						start = i;
					}
					index++;
				}
			}
			return customizeText(map, replace);
		}
		return null;
	}

	public static SpannableStringBuilder customizeText(String text, int color, char c) {
		if(text != null) {
			int index = 0;
			int start = 0;
			String replace = text.replace(String.valueOf(c), "");
			int length = replace.length();
			ArrayList<SpannableMap> map = new ArrayList<>();
			for(int i = 0; i < text.length(); i++) {
				if(text.charAt(i) == c) {
					if(index % 2 != 0) {
						int adj = map.size() * 2;
						int end = i - adj - 1;
						start -= adj;
						end = Math.min(end, length);
						map.add(new SpannableMap(start, end, color));
					}
					else {
						start = i;
					}
					index++;
				}
			}
			return customizeText(map, replace);
		}
		return null;
	}

	public static boolean withGooglePlayServices(final Activity activity) {
		boolean result = false;
		final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if(resultCode != ConnectionResult.SUCCESS) {
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 1);
				if(dialog != null) {
					dialog.show();
					dialog.setCancelable(true);
					dialog.setOnDismissListener(dialog12 -> {
						if(ConnectionResult.SERVICE_INVALID == resultCode) {
							activity.finish();
						}
					});
					dialog.setOnCancelListener(dialog1 -> activity.finish());
				}
			}
		}
		else {
			result = true;
		}
		return result;
	}

	public static String validateURL(String url) {
		String https = "https://";
		String http = "http://";
		if(url != null) {
			if(!url.contains(https) && !url.contains(http)) {
				return https + url;
			}
		}
		return url;
	}

	public static void clearImageUrl(Context context, String url) {
		ImageLoader imageLoader = getImageLoader(context);
		MemoryCacheUtils.removeFromCache(url, imageLoader.getMemoryCache());
		DiskCacheUtils.removeFromCache(url, imageLoader.getDiskCache());
	}

	public static void clearImageCache(Context context) {
		ImageLoader imageLoader = getImageLoader(context);
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
	}

	public static ImageLoader getImageLoader(Context context) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		return imageLoader;
	}

	public static DisplayImageOptions buildOptions(int placeholder, boolean fadeIn) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		if(fadeIn) {
			builder.displayer(new FadeInBitmapDisplayer(250));
		}
		if(placeholder != 0) {
			builder.showImageOnLoading(placeholder);
			builder.showImageForEmptyUri(placeholder);
		}
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		return builder.build();
	}

	public static void displayImage(ImageView view, String uri) {
		if(view != null) {
			ImageLoader imageLoader = getImageLoader(view.getContext());
			DisplayImageOptions options = buildOptions(0, true);
			imageLoader.displayImage(uri, view, options);
		}
	}

	public static void displayImage(ImageView view, String uri, ImageLoadingListener listener) {
		if(view != null) {
			ImageLoader imageLoader = getImageLoader(view.getContext());
			DisplayImageOptions options = buildOptions(0, true);
			imageLoader.displayImage(uri, view, options, listener);
		}
	}

	public static void displayImage(ImageView view, String uri, int placeholder) {
		if(view != null) {
			ImageLoader imageLoader = getImageLoader(view.getContext());
			DisplayImageOptions options = buildOptions(placeholder, true);
			imageLoader.displayImage(uri, view, options);
		}
	}

	public static void displayImage(ImageView view, String uri, int placeholder,
		ImageLoadingListener listener) {
		if(view != null) {
			ImageLoader imageLoader = getImageLoader(view.getContext());
			DisplayImageOptions options = buildOptions(placeholder, true);
			imageLoader.displayImage(uri, view, options, listener);
		}
	}

	public static void displayImage(ImageView view, String uri, int placeholder,
		ImageLoadingListener listener, ImageLoadingProgressListener progress) {
		if(view != null) {
			ImageLoader imageLoader = getImageLoader(view.getContext());
			DisplayImageOptions options = buildOptions(placeholder, true);
			imageLoader.displayImage(uri, view, options, listener, progress);
		}
	}

	public static Bitmap getBitmap(Context context, String uri) {
		ImageLoader imageLoader = getImageLoader(context);
		return imageLoader.loadImageSync(uri);
	}

	public static GpsData getGps(
		Context context, Location location, TimeZone timeZone,
		long lastLocationUpdate, long interval,
		float requiredAccuracy, long timeDiffAllowance
	) {
		GpsData gps = new GpsData();
		gps.isEnabled = isGpsEnabled(context);
		gps.dt = new DateTime();
		if(location != null) {
			String provider = location.getProvider();
			long timestamp = location.getTime();
			gps.latitude = location.getLatitude();
			gps.longitude = location.getLongitude();
			gps.altitude = location.getAltitude();
			gps.accuracy = location.getAccuracy();
			gps.speed = location.getSpeed();
			gps.bearing = location.getBearing();
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				gps.isMock = location.isMock();
			}
			else {
				gps.isMock = location.isFromMockProvider();
			}
			gps.isIndoor = provider != null && !provider.equals(LocationManager.GPS_PROVIDER);
			final long timeElapsed = SystemClock.elapsedRealtime() - lastLocationUpdate;
			final long elapsedAllowance = 15000L + interval;
			if(timeElapsed <= elapsedAllowance && (gps.accuracy <= requiredAccuracy || !gps.isIndoor)) {
				if(gps.longitude != 0 && gps.latitude != 0) {
					gps.isValid = !gps.isMock;
				}
			}
			TimeZone utc = TimeZone.getTimeZone("UTC");
			DateTime dt = DateTime.Companion.fromTimestamp(timestamp, utc);
			gps.dt = dt.to(timeZone);
			if(gps.isValid) {
				final DateTime now = DateTime.Companion.nowIn(utc);
				final long difference = now.difference(dt);
				if(Math.abs(difference) > timeDiffAllowance) {
					gps.isValid = false;
				}
			}
			gps.location = location;
			gps.withHistory = true;
		}
		return gps;
	}

	public static boolean isOnBackStack(FragmentActivity activity, String tag) {
		FragmentManager manager = activity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(tag);
		return fragment != null && fragment.isVisible();
	}

	public static void setCrashHandler(final Context context, String folder, String password) {
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, folder, password));
	}

	public static String capitalizeWord(String text) {
		if(text != null && !text.isEmpty()) {
			String[] words = text.trim().split(" ");
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < words.length; i++) {
				if(words[i].trim().length() > 0) {
					builder.append(Character.toUpperCase(words[i].trim().charAt(0)));
					builder.append(words[i].trim().substring(1));
					if(i < words.length - 1) {
						builder.append(' ');
					}
				}
			}
			return builder.toString();
		}
		return text;
	}

	public static String capitalizeSentence(String text) {
		if(text != null) {
			return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
		}
		return null;
	}

	public static boolean isViewVisible(ScrollView sv, View v) {
		Rect rect = new Rect();
		sv.getHitRect(rect);
		return v.getLocalVisibleRect(rect);
	}

	public static String getRawString(Context context, int res) {
		String result = null;
		Resources resources = context.getResources();
		try {
			InputStream is = resources.openRawResource(res);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while((length = is.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			result = bos.toString("UTF-8");
			is.close();
			bos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String handleSQLite(String text) {
		if(text != null) {
			return text.replace("''", "'");
		}
		return null;
	}

	public static void sendSMS(String address, String message) {
		if(message != null && !message.isEmpty() &&
			address != null && !address.isEmpty()) {
			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(address, null, message, null, null);
		}
	}

	public static void sendSMS(String address, String message, PendingIntent si) {
		if(message != null && !message.isEmpty() &&
			address != null && !address.isEmpty()) {
			SmsManager manager = SmsManager.getDefault();
			ArrayList<String> parts = manager.divideMessage(message);
			ArrayList<PendingIntent> siList = new ArrayList<>();
			for(String part : parts) {
				siList.add(si);
			}
			manager.sendMultipartTextMessage(address, null, parts, siList, null);
		}
	}


	public static boolean isValidMobile(String mobileNo) {
		if(mobileNo != null) {
			int length = mobileNo.length();
			if(length >= 10 && length <= 13) {
				if(length == 10) {
					char first = mobileNo.charAt(0);
					if(first == '0') {
						return false;
					}
				}
				String text = mobileNo.replace("+", "");
				return isNumeric(text);
			}
		}
		return false;
	}

	public static Spanned underlineText(String text) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return Html.fromHtml("<u>" + text + "</u>", Html.FROM_HTML_MODE_LEGACY);
		}
		else {
			return Html.fromHtml("<u>" + text + "</u>");
		}
	}

	public static void e(String TAG, String message) {
		int maxLogSize = 2000;
		for(int i = 0; i <= message.length() / maxLogSize; i++) {
			int start = i * maxLogSize;
			int end = (i + 1) * maxLogSize;
			end = end > message.length() ? message.length() : end;
			android.util.Log.e(TAG, message.substring(start, end));
		}
	}

	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
			|| Build.FINGERPRINT.startsWith("unknown")
			|| Build.FINGERPRINT.contains("userdebug/test-keys")
			|| Build.MODEL.contains("google_sdk")
			|| Build.MODEL.contains("Emulator")
			|| Build.MODEL.contains("Android SDK built for x86")
			|| Build.BOARD.equals("QC_Reference_Phone")
			|| Build.MANUFACTURER.contains("Genymotion")
			|| Build.MANUFACTURER.contains("Genymobile")
			|| Build.HOST.startsWith("Build")
			|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
			|| "google_sdk".equals(Build.PRODUCT);
	}

	public static boolean isPhoneRooted() {
		try {
			File apk = new File("/system/app/Superuser.apk");
			if(apk.exists()) {
				return true;
			}
			else {
				File su = new File("/system/xbin/su");
				return su.exists();
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static void setSharedPref(Context context, String key, boolean value) {
		String name = context.getPackageName();
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public static void setSharedPref(Context context, String key, int value) {
		String name = context.getPackageName();
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public static void setSharedPref(Context context, String key, String value) {
		String name = context.getPackageName();
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void setSharedPref(Context context, String key, float value) {
		String name = context.getPackageName();
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.apply();
	}

	public static void setSharedPref(Context context, String key, long value) {
		String name = context.getPackageName();
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.apply();
	}

	public static boolean getBooleanSharedPref(Context context, String key) {
		String name = context.getPackageName();
		SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, false);
	}

	public static int getIntSharedPref(Context context, String key) {
		String name = context.getPackageName();
		SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return prefs.getInt(key, 0);
	}

	public static String getStringSharedPref(Context context, String key) {
		String name = context.getPackageName();
		SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return prefs.getString(key, null);
	}

	public static float getFloatSharedPref(Context context, String key) {
		String name = context.getPackageName();
		SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return prefs.getFloat(key, 0F);
	}

	public static long getLongSharedPref(Context context, String key) {
		String name = context.getPackageName();
		SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return prefs.getLong(key, 0);
	}

	public static void forceStop() {
		int processID = android.os.Process.myPid();
		android.os.Process.killProcess(processID);
	}

	public static LatLng travel(double latitude, double longitude, double bearing,
		double distance) {
		final double earth = 6371000D;
		double b = Math.toRadians(bearing);
		double latIn = Math.toRadians(latitude);
		double lonIn = Math.toRadians(longitude);
		double dr = distance / earth;
		double a = Math.sin(dr) * Math.cos(latIn);
		double latOut = Math.asin(Math.sin(latIn) * Math.cos(dr) + a * Math.cos(b));
		double lonOut = lonIn + Math.atan2(Math.sin(b) * a, Math.cos(dr) - Math.sin(latIn) * Math.sin(latOut));
		return new LatLng(Math.toDegrees(latOut), Math.toDegrees(lonOut));
	}

	public static ArrayList<SystemMediaData> loadAllImages(Context context) {
		ArrayList<SystemMediaData> mediaList = new ArrayList<>();
		String[] projection = {
			MediaStore.MediaColumns._ID,
			MediaStore.MediaColumns.DATA,
			MediaStore.MediaColumns.DATE_ADDED,
			MediaStore.MediaColumns.DISPLAY_NAME,
			MediaStore.MediaColumns.MIME_TYPE,
			MediaStore.MediaColumns.SIZE,
		};
		boolean isDeprecated = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
		Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
			null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
		if(cursor != null) {
			while(cursor.moveToNext()) {
				int idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
				int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
				int dateIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
				int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
				int mimeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
				int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
				SystemMediaData media = new SystemMediaData();
				media.id = cursor.getLong(idIndex);
				media.uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, cursor.getLong(idIndex));
				if(!isDeprecated) {
					media.path = cursor.getString(pathIndex);
				}
				media.date = cursor.getString(dateIndex);
				media.displayName = cursor.getString(nameIndex);
				media.mimeType = cursor.getString(mimeIndex);
				media.fileSize = cursor.getInt(sizeIndex);
				mediaList.add(media);
			}
			cursor.close();
		}
		return mediaList;
	}

	public static String getFilePath(Context context, Uri uri) {
		String path = null;
		ContentResolver resolver = context.getContentResolver();
		String[] projection = {Media.DATA};
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		if(cursor != null) {
			int index = cursor.getColumnIndex(Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(index);
			cursor.close();
		}
		return path;
	}

	public static Bitmap stampPhoto(
		Context context,
		Bitmap input,
		String font,
		float textSizePercentage,
		ArrayList<StampData> stampList
	) {
		int width = input.getWidth();
		int height = input.getHeight();
		int max = Math.max(width, height);
		int min = Math.min(width, height);
		float percentage = textSizePercentage != 0 ? textSizePercentage : 0.035F;
		final int textSize = (int) (min * percentage);
		Console.log("size: " + textSize + " vs " + min);
		final int margin = (int) (((float) textSize) * 0.6F);
		float rt = (float) textSize / (float) max;
		float rm = (float) margin / (float) max;
		float m = rm * (float) max;
		float size = rt * (float) max;
		Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		final int shadow = Color.argb(96, 0, 0, 0);
		Typeface tf = TypefaceCache.get(context, font);
		TextPaint paint = new TextPaint();
		paint.setAntiAlias(true);
		paint.setTypeface(tf);
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
		paint.setShadowLayer(2F, 2F, 2F, shadow);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawBitmap(input, 0F, 0F, null);
		int lc = 0;
		int rc = 0;
		for(StampData stamp : stampList) {
			if(stamp.alignment != null) {
				switch(stamp.alignment) {
					case LEFT:
						lc++;
						break;
					case RIGHT:
						rc++;
						break;
				}
			}
		}
		float yl = height - (size * lc) + (m / 2);
		float yr = height - (size * rc) + (m / 2);
		for(StampData stamp : stampList) {
			if(stamp.alignment != null) {
				paint.setTextAlign(stamp.alignment);
				switch(stamp.alignment) {
					case LEFT:
						canvas.drawText(stamp.data, m, yl, paint);
						yl += size;
						break;
					case RIGHT:
						canvas.drawText(stamp.data, width - m, yr, paint);
						yr += size;
						break;
				}
			}
		}
		return result;
	}

	public static Bitmap createBitmapFromView(View view) {
		if(view != null) {
			int width = getWidth(view);
			int height = getHeight(view);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(params);
			view.layout(0, 0, width, height);
			view.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
			return bitmap;
		}
		return null;
	}

	public static String text(
		Context context,
		int resId,
		String... placeholders
	) {
		String text = context.getString(resId);
		return replacePlaceholder(text, placeholders);
	}

	public static String text(
		Context context,
		int resId,
		boolean isSpannable,
		boolean withQuotes,
		String... placeholders
	) {
		String text = context.getString(resId);
		if(isSpannable) {
			String[] spannablePlaceholders = new String[placeholders.length];
			for(int i = 0; i < placeholders.length; i++) {
				if(withQuotes) {
					spannablePlaceholders[i] = "$\"" + placeholders[i] + "\"$";
				}
				else {
					spannablePlaceholders[i] = "$" + placeholders[i] + "$";
				}
			}
			return replacePlaceholder(text, spannablePlaceholders);
		}
		return replacePlaceholder(text, placeholders);
	}

	public static String replacePlaceholder(String text, String... placeholders) {
		final String key = "$";
		if(text != null && text.contains(" ")) {
			StringBuilder result = new StringBuilder();
			String[] array = text.split(" ");
			int index = 0;
			int size = array.length;
			for(int i = 0; i < size; i++) {
				String word = array[i];
				if(word.contains(key)) {
					int k = word.indexOf(key);
					String sub = word.substring(k);
					String code = key + sub.replaceAll("[^a-zA-z0-9]", "");
					String placeholder = index < placeholders.length ?
						placeholders[index] : placeholders[0];
					word = word.replace(code, placeholder);
					index++;
				}
				String w = i < size - 1 ? word + " " : word;
				result.append(w);
			}
			return result.toString();
		}
		return text;
	}

	public static boolean contentUriToFile(Context context, Uri uri, File file) {
		boolean result = false;
		try {
			InputStream is = context.getContentResolver().openInputStream(uri);
			FileOutputStream fos = new FileOutputStream(file);
			int read;
			byte[] bytes = new byte[1024];
			while((read = is.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
			result = true;
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static float getDimensionInDp(Context context, int resId) {
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		float size = res.getDimension(resId);
		return size / dm.density;
	}

	public static String millisToDuration(long millis) {
		long max = 60L;
		long seconds = millis / 1000L;
		long minutes = 0L;
		long hours = 0L;
		if(seconds > max) {
			minutes = seconds / max;
			seconds = seconds % max;
			if(minutes > max) {
				hours = minutes / max;
				minutes = minutes % max;
			}
		}
		String h = String.format(Locale.ENGLISH, "%02d", hours);
		String m = String.format(Locale.ENGLISH, "%02d", minutes);
		String s = String.format(Locale.ENGLISH, "%02d", seconds);
		String duration = m + ":" + s;
		if(hours != 0) {
			duration = h + ":" + duration;
		}
		return duration;
	}

	public static void enableFullscreen(Activity activity) {
		Window window = activity.getWindow();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			View decorView = window.getDecorView();
			final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_FULLSCREEN |
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(flags);
		}
		else {
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	public static void changeOrientation(Activity activity, boolean isLandscape) {
		if(isLandscape) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public static void disableFullscreen(Activity activity) {
		Window window = activity.getWindow();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			View decorView = window.getDecorView();
			final int flags = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(flags);
		}
		else {
			window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	public static boolean hasData(String text) {
		if(text != null) {
			return !text.equals("null") && !text.isEmpty() && !text.equals("0")
				&& !text.equalsIgnoreCase("na")
				&& !text.equals("n/a");
		}
		return false;
	}

	public static Bitmap getScreenshot(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}

	public static Bitmap getScreenshot(View v, int width, int height) {
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, width, height);
		v.draw(c);
		return b;
	}

	@Deprecated
	public static ArrayList<String> removeDuplicateEntry(ArrayList<String> entryList) {
		if(entryList != null) {
			ArrayList<String> holderList = new ArrayList<>();
			for(String entry : entryList) {
				if(!holderList.contains(entry)) {
					holderList.add(entry);
				}
			}
			entryList.clear();
			entryList.addAll(holderList);
		}
		return entryList;
	}

	public static boolean isValidDate(String date) {
		if(date != null && !date.isEmpty()) {
			try {
				final String PATTERN = "yyyy-MM-dd";
				SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
				format.parse(date);
				return true;
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean isValidTime(String time) {
		if(time != null && !time.isEmpty()) {
			try {
				final String PATTERN = "HH:mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
				format.parse(time);
				return true;
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String censorMobileNo(String mobileNo) {
		if(mobileNo != null) {
			final int length = mobileNo.length();
			final int lastDigits = 4;
			String censored = "";
			for(int i = length - 1; i >= 0; i--) {
				char c = mobileNo.charAt(i);
				if(i >= length - lastDigits) {
					censored = c + censored;
				}
				else {
					censored = "*" + censored;
				}
			}
			return censored;
		}
		return null;
	}

	public static boolean withinMonthlyCutOffExact(String date, int cutOff,
		HashMap<String, String> feedback) {
		if(cutOff != 0 && cutOff <= 28) {
			String current = getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dateToMillis(current));
			int today = cal.get(Calendar.DAY_OF_MONTH);
			if(today <= cutOff) {
				cal.roll(Calendar.MONTH, -1);
				if(cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
					cal.roll(Calendar.YEAR, -1);
				}
			}
			cal.roll(Calendar.DAY_OF_MONTH, cutOff - today);
			cal.roll(Calendar.DAY_OF_MONTH, 1);
			String startDate = String.format(Locale.ENGLISH, "%tF", cal);
			cal.roll(Calendar.MONTH, 1);
			cal.roll(Calendar.DAY_OF_MONTH, -1);
			String endDate = String.format(Locale.ENGLISH, "%tF", cal);
			if(feedback != null) {
				feedback.put("startDate", startDate);
				feedback.put("endDate", endDate);
			}
			return isDateBetween(date, startDate, endDate);
		}
		throw new IllegalArgumentException("Cut-off period must be between 1 to 28.");
	}

	public static boolean withinMonthlyCutOff(String date, int cutOff,
		HashMap<String, String> feedback) {
		if(cutOff != 0 && cutOff <= 28) {
			String current = getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dateToMillis(current));
			int today = cal.get(Calendar.DAY_OF_MONTH);
			if(today <= cutOff) {
				cal.roll(Calendar.MONTH, -1);
				if(cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
					cal.roll(Calendar.YEAR, -1);
				}
			}
			cal.set(Calendar.DAY_OF_MONTH, 1);
			String startDate = String.format(Locale.ENGLISH, "%tF", cal);
			if(feedback != null) {
				feedback.put("startDate", startDate);
				feedback.put("endDate", current);
			}
			return isDateBetween(date, startDate, current);
		}
		throw new IllegalArgumentException("Cut-off period must be between 1 to 28.");
	}

	public static boolean withinWeeklyCutOff(String date, int cutOff,
		HashMap<String, String> feedback) {
		if(cutOff != 0 && cutOff <= 7) {
			String current = getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dateToMillis(current));
			int today = cal.get(Calendar.DAY_OF_WEEK);
			int rollCount = 0;
			if(today <= cutOff) {
				int difference = cutOff - today;
				rollCount = 7 - difference - 1;
			}
			else {
				int difference = today - cutOff;
				rollCount = difference - 1;
			}
			String after = rollDate(current, -rollCount);
			cal.setTimeInMillis(dateToMillis(after));
			String startDate = String.format(Locale.ENGLISH, "%tF", cal);
			if(feedback != null) {
				feedback.put("startDate", startDate);
				feedback.put("endDate", current);
			}
			return isDateBetween(date, startDate, current);
		}
		throw new IllegalArgumentException("Cut-off period must be between 1 to 7 " +
			"indicating the numeric value of each weekday " +
			"Su=1, Mo=2, Tu=3, We=4, Th=5, Fr=6, Sa=7.");
	}

	public static boolean withinDailyCutOff(String date, String cutOff) {
		if(cutOff != null && cutOff.split(":").length == 3) {
			String startDate = getDate();
			String time = getTime();
			if(timeToMillis(time) > timeToMillis(cutOff)) {
				startDate = rollDate(startDate, 1);
			}
			return isDateOnOrAfter(date, startDate);
		}
		throw new IllegalArgumentException("Cut-off must be a 24 hour (HH:mm:ss) format time.");
	}

	public static int getDayOfTheWeek(String day) {
		if(day != null && day.length() >= 2) {
			String key = day.substring(0, 2).toLowerCase();
			switch(key) {
				case "su":
					return 1;
				case "mo":
					return 2;
				case "tu":
					return 3;
				case "we":
					return 4;
				case "th":
					return 5;
				case "fr":
					return 6;
				case "sa":
					return 7;
			}
		}
		return 0;
	}

	public static boolean isDateAfter(String date1, String date2) {
		long date1Millis = dateToMillis(date1);
		long date2Millis = dateToMillis(date2);
		return date1Millis > date2Millis;
	}

	public static boolean isDateBefore(String date1, String date2) {
		long date1Millis = dateToMillis(date1);
		long date2Millis = dateToMillis(date2);
		return date1Millis < date2Millis;
	}

	public static boolean isDateOnOrAfter(String date1, String date2) {
		long date1Millis = dateToMillis(date1);
		long date2Millis = dateToMillis(date2);
		return date1Millis >= date2Millis;
	}

	public static boolean isDateOnOrBefore(String date1, String date2) {
		long date1Millis = dateToMillis(date1);
		long date2Millis = dateToMillis(date2);
		return date1Millis <= date2Millis;
	}

	public static boolean isDateEquals(String date1, String date2) {
		long date1Millis = dateToMillis(date1);
		long date2Millis = dateToMillis(date2);
		return date1Millis == date2Millis;
	}

	public static boolean isDateBetween(String date, String startDate, String endDate) {
		return isDateOnOrAfter(date, startDate) && isDateOnOrBefore(date, endDate);
	}

	public static LatLng geocodeAddress(Context context, String address) {
		if(hasInternet(context) && Geocoder.isPresent() && address != null) {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			try {
				List<Address> coordinates = geocoder.getFromLocationName(address, 1);
				if(coordinates != null && !coordinates.isEmpty()) {
					Address coordinate = coordinates.get(0);
					double longitude = coordinate.getLongitude();
					double latitude = coordinate.getLatitude();
					return new LatLng(latitude, longitude);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Place reverseGeocode(Context context, double latitude, double longitude) {
		if(Geocoder.isPresent()) {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			try {
				List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
				if(addressList != null && !addressList.isEmpty()) {
					Address address = addressList.get(0);
					if(address != null) {
						return new Place(address);
					}
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean containsLetters(String text) {
		Pattern pattern = Pattern.compile("[A-Za-z]");
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	public static boolean containsNumbers(String text) {
		Pattern pattern = Pattern.compile("[0-9]");
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	public static boolean containsSpecialCharacters(String text) {
		Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	public static String replaceVariableInEquation(
		String input,
		String variableName,
		String value
	) {
		return input.replaceAll("\\b" + variableName + "\\b", value);
	}

	public static Bitmap getBitmapImage(Context context, Uri uri) {
		try {
			ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
			if(pfd != null) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 2;
				return BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setSoftInputModeToAdjustResize(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public static void setSoftInputModeToAdjustPan(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public static int getMax(String... numbers) {
		int max = 0;
		if(numbers != null) {
			for(String element : numbers) {
				if(isNumeric(element)) {
					final int value = Integer.parseInt(element);
					if(value > max) {
						max = value;
					}
				}
			}
		}
		return max;
	}

	public static int getMin(String... numbers) {
		int min = 0;
		if(numbers != null) {
			for(String element : numbers) {
				if(isNumeric(element)) {
					final int value = Integer.parseInt(element);
					if(value < min) {
						min = value;
					}
				}
			}
		}
		return min;
	}

	public static int getMax(int... numbers) {
		int max = 0;
		if(numbers != null) {
			for(final int value : numbers) {
				if(value > max) {
					max = value;
				}
			}
		}
		return max;
	}

	public static int getMin(int... numbers) {
		int min = 0;
		if(numbers != null) {
			for(final int value : numbers) {
				if(value < min) {
					min = value;
				}
			}
		}
		return min;
	}

	public static boolean hasTelephonyFeature(Context context) {
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	public static String readFromFile(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String toMultiline(String input, int maxLength) {
		final String space = " ";
		final String[] words = input.split(space);
		int current = 0;
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < words.length; i++) {
			final String word = words[i];
			current += word.length();
			if(current <= maxLength) {
				builder.append(word);
			}
			else {
				builder.append("\n");
				builder.append(word);
				current = word.length();
			}
			if(i < words.length - 1) {
				builder.append(space);
				current += 1;
			}
		}
		return builder.toString();
	}

	public static String groupNumbers(String text) {
		final StringBuilder builder = new StringBuilder();
		String whole = text;
		String decimal = "";
		if(text.contains(".")) {
			int index = text.indexOf(".");
			decimal = text.substring(index);
			whole = text.substring(0, index);
		}
		int count = 0;
		for(int i = whole.length() - 1; i >= 0; i--) {
			char c = whole.charAt(i);
			builder.append(c);
			count++;
			if(count == 3 && i != 0) {
				builder.append(",");
				count = 0;
			}
		}
		return builder.reverse().append(decimal).toString();
	}

	public static boolean isValidImage(File file) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			if(options.outWidth != -1 && options.outHeight != -1) {
				return true;
			}
		}
		catch(
			Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}