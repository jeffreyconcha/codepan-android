package com.codepan.app;

import android.os.Bundle;

import com.codepan.callback.Interface.OnInitializeCallback;

public abstract class CPSplashFragment extends CPFragment {

	private OnInitializeCallback initializeCallback;
	protected boolean isPause;

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onInitialize(initializeCallback);
	}

	public void setOnInitializeCallback(OnInitializeCallback initializeCallback) {
		this.initializeCallback = initializeCallback;
	}

	@Override
	public void onBackPressed() {
		activity.finish();
	}

	protected abstract void onInitialize(OnInitializeCallback initializeCallback);
}
