package com.codepan.utils;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

import androidx.annotation.NonNull;

public class CrashHandler implements UncaughtExceptionHandler {

	private Context context;
	private String folder, password;

	public CrashHandler(Context context, String folder, String password) {
		this.context = context;
		this.folder = folder;
		this.password = password;
	}

	@Override
	public void uncaughtException(@NonNull Thread thread, Throwable e) {
		String message = e.getMessage() + "\n" + CodePanUtils.throwableToString(e);
		CodePanUtils.setErrorMsg(context, message, folder, password);
		try {
			throw e;
		}
		catch(Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
}
