package com.codepan.utils;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

import androidx.annotation.NonNull;

public class CrashHandler implements UncaughtExceptionHandler {

	private boolean shouldRethrow;
	private String folder, password;
	private Context context;

	public CrashHandler(Context context, String folder, String password, boolean shouldRethrow) {
		this.context = context;
		this.folder = folder;
		this.password = password;
		this.shouldRethrow = shouldRethrow;
	}

	@Override
	public void uncaughtException(@NonNull Thread thread, Throwable e) {
		String message = e.getMessage() + "\n" + CodePanUtils.throwableToString(e);
		CodePanUtils.setErrorMsg(context, message, folder, password);
		if(!shouldRethrow) {
			System.exit(0);
		}
	}
}
