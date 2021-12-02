package com.codepan.app;

import android.os.Bundle;
import android.view.KeyEvent;

import com.codepan.app.CPFragmentActivity.KeyListener;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.utils.CodePanUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CPFragment extends Fragment implements OnBackPressedCallback, KeyListener {

	private OnFragmentCallback fragmentCallback;
	protected FragmentTransaction transaction;
	protected CPFragmentActivity activity;
	protected FragmentManager manager;
	protected boolean withChanges;
	private boolean isDisabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (CPFragmentActivity) getActivity();
		if(activity != null) {
			manager = activity.getSupportFragmentManager();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
//		if(!hidden) {
//			activity.setKeyListener(this);
//		}
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
//		activity.setKeyListener(this);
	}

	/**
	 * <b>Note</b>: Only use this if the current fragment is not going
	 * to call @{@link FragmentTransaction#hide(Fragment)}
	 */
	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	protected void disableBackPressed() {
		this.isDisabled = true;
	}

	protected void enableBackPressed() {
		this.isDisabled = false;
	}

	protected String text(int resId, String... placeholders) {
		return CodePanUtils.text(activity, resId, placeholders);
	}

	protected String text(int resId, boolean isSpannable, boolean withQuotes, String... placeholders) {
		return CodePanUtils.text(activity, resId, isSpannable, withQuotes, placeholders);
	}

	@Override
	public boolean onKeyUp(int code, KeyEvent event) {
		if(!isDisabled && code == KeyEvent.KEYCODE_BACK) {
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

	public KeyListener getKeyListener() {
		return this;
	}
}
