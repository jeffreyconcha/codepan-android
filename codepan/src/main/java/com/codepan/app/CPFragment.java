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

public class CPFragment extends Fragment implements OnBackPressedCallback,
	OnFragmentCallback, KeyListener {

	private OnFragmentCallback fragmentCallback;
	private boolean isDisabled, hasBackPressed;
	protected FragmentTransaction transaction;
	protected CPFragmentActivity activity;
	protected FragmentManager manager;
	protected boolean withChanges;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (CPFragmentActivity) getActivity();
		if(activity != null) {
			manager = activity.getSupportFragmentManager();
		}
	}

//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		if(!isDisabled) {
//			activity.overrideBackPressed(true);
//			activity.setOnBackPressedCallback(this);
//		}
//	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
//		if(!isDisabled) {
//			if(!hidden) {
//				activity.setOverrideLocked(true);
//				activity.setOnBackPressedCallback(this);
//				activity.overrideBackPressed(true);
//			}
//			else {
//				activity.setOnBackPressedCallback(null);
//				activity.overrideBackPressed(false);
//			}
//		}
		if(!hidden) {
			activity.setKeyListener(this);
		}
	}

//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		if(!isDisabled) {
//			if(fragmentCallback == null) {
//				if(!activity.isOverrideLocked()) {
//					activity.setOnBackPressedCallback(null);
//					activity.overrideBackPressed(false);
//				}
//				else {
//					activity.setOverrideLocked(false);
//				}
//			}
//		}
//	}

	@Override
	public void onBackPressed() {
		if(manager != null) {
			manager.popBackStack();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
//		if(fragmentCallback != null) {
//			fragmentCallback.onFragment(true, hasBackPressed);
//		}
		activity.setKeyListener(this);
	}

//	@Override
//	public void onStop() {
//		super.onStop();
//		if(fragmentCallback != null) {
//			fragmentCallback.onFragment(false, hasBackPressed);
//		}
//	}

	@Override
	public void onFragment(boolean isActive, boolean hasBackPressed) {
//		if(!isDisabled) {
//			if(isActive) {
//				activity.overrideBackPressed(hasBackPressed);
//			}
//			else {
//				activity.setOnBackPressedCallback(this);
//				activity.overrideBackPressed(true);
//			}
//		}
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

	public void setHasBackPressed(boolean hasBackPressed) {
		this.hasBackPressed = hasBackPressed;
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
}
