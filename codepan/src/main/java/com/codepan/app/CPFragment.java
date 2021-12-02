package com.codepan.app;

import android.os.Bundle;
import android.view.KeyEvent;

import com.codepan.app.CPFragmentActivity.KeyListener;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.utils.CodePanUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CPFragment extends Fragment implements OnBackPressedCallback, KeyListener {

	protected FragmentTransaction transaction;
	private boolean isBackPressedDisabled;
	protected CPFragmentActivity activity;
	protected FragmentManager manager;
	private KeyListener keyListener;
	protected boolean withChanges;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (CPFragmentActivity) getActivity();
		if(activity != null) {
			manager = activity.getSupportFragmentManager();
		}
	}

	@Override
	public void onBackPressed() {
		if(manager != null) {
			manager.popBackStack();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		keyListener = activity.getKeyListener();
		activity.setKeyListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		activity.setKeyListener(keyListener);
	}

	protected void disableBackPressed() {
		this.isBackPressedDisabled = true;
	}

	protected void enableBackPressed() {
		this.isBackPressedDisabled = false;
	}

	public boolean isBackPressedDisabled() {
		return this.isBackPressedDisabled;
	}

	protected String text(int resId, String... placeholders) {
		return CodePanUtils.text(activity, resId, placeholders);
	}

	protected String text(int resId, boolean isSpannable, boolean withQuotes, String... placeholders) {
		return CodePanUtils.text(activity, resId, isSpannable, withQuotes, placeholders);
	}

	@Override
	public boolean onKeyUp(int code, KeyEvent event) {
		if(!isBackPressedDisabled && code == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int code, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyLongPress(int code, KeyEvent event) {
		return false;
	}
}
