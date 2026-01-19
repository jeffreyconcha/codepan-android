package com.codepan.utils;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

import androidx.annotation.NonNull;

public class CrashHandler implements UncaughtExceptionHandler {

	private final Thread.UncaughtExceptionHandler defaultHandler;
	private final String folder, password;
	private final Context context;

	public CrashHandler(
		Context context,
		String folder,
		String password,
		UncaughtExceptionHandler defaultHandler
	) {
		this.context = context;
		this.folder = folder;
		this.password = password;
		this.defaultHandler = defaultHandler;
	}

	@Override
	public void uncaughtException(@NonNull Thread t, Throwable e) {
		String message = e.getMessage() + "\n" + CodePanUtils.throwableToString(e);
		CodePanUtils.setErrorMsg(context, message, folder, password);
		defaultHandler.uncaughtException(t, e);
	}
}
