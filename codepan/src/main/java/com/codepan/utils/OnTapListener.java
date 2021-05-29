package com.codepan.utils;

import android.os.Handler;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
public abstract class OnTapListener implements View.OnClickListener {

	private Timer timer;
	private final int DELAY = 400;
	private final long DELTA = 300;
	private long lastMillis = 0;

	@Override
	public void onClick(final View v) {
		long current = System.currentTimeMillis();
		long difference = current - lastMillis;
		if(difference < DELTA) {
			if(timer != null) {
				timer.cancel();
				timer.purge();
			}
			onDoubleTap(v);
		}
		else {
			final Handler handler = new Handler();
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					onSingleTap(v);
				}
			};
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					handler.post(runnable);
				}
			};
			timer = new Timer();
			timer.schedule(task, DELAY);
		}
		lastMillis = current;
	}

	public abstract void onSingleTap(View v);

	public abstract void onDoubleTap(View v);
}
