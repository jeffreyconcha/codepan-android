package com.codepan.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.codepan.callback.Interface.OnInitializeCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.permission.PermissionHandler;
import com.codepan.utils.CodePanUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class CPFragmentActivity extends FragmentActivity
	implements OnInitializeCallback {

	interface KeyListener {
		boolean onKeyUp(int code, KeyEvent event);

		boolean onKeyDown(int code, KeyEvent event);

		boolean onKeyLongPress(int code, KeyEvent event);
	}

	private OnRequestPermissionsResultCallback permissionsResultCallback;
	protected FragmentTransaction transaction;
	protected FragmentManager manager;
	private PermissionHandler handler;
	private KeyListener keyListener;
	private boolean isInitialized;
	protected SQLiteAdapter db;

	@Override
	protected void onResume() {
		super.onResume();
		if(handler != null) {
			handler.checkPermissions();
			handler = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.manager = getSupportFragmentManager();
		if(savedInstanceState != null) {
			if(!isInitialized) {
				overridePendingTransition(0, 0);
				restartActivity();
			}
		}
		else {
			onLoadSplash(this);
		}
	}

	private void restartActivity() {
		Intent intent = new Intent(this, getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		this.finish();
	}

	public abstract void onLoadSplash(OnInitializeCallback initializeCallback);

	public boolean isInitialized() {
		return this.isInitialized;
	}

	public void setOnRequestPermissionsResultCallback(OnRequestPermissionsResultCallback permissionsResultCallback) {
		this.permissionsResultCallback = permissionsResultCallback;
	}

	@Override
	public void onInitialize(SQLiteAdapter db) {
		this.isInitialized = true;
		this.db = db;
	}

	@Override
	public void onRequestPermissionsResult(int code, @NonNull String[] permissions,
			@NonNull int[] results) {
		if(permissionsResultCallback != null) {
			permissionsResultCallback.onRequestPermissionsResult(code, permissions, results);
		}
	}

	protected String text(int resId, String... placeholders) {
		return CodePanUtils.text(this, resId, placeholders);
	}

	public void setHandler(PermissionHandler handler) {
		this.handler = handler;
	}

	public boolean inBackStack(String tag) {
		Fragment fragment = manager.findFragmentByTag(tag);
		return fragment != null && fragment.isVisible();
	}

	public boolean notInBackStack(String tag) {
		return !inBackStack(tag);
	}

	@Override
	public boolean onKeyUp(int code, KeyEvent event) {
		if(keyListener != null && keyListener.onKeyUp(code, event)) {
			return true;
		}
		return super.onKeyUp(code, event);
	}

	@Override
	public boolean onKeyDown(int code, KeyEvent event) {
		if(keyListener != null && keyListener.onKeyDown(code, event)) {
			return true;
		}
		return super.onKeyDown(code, event);
	}

	@Override
	public boolean onKeyLongPress(int code, KeyEvent event) {
		if(keyListener != null && keyListener.onKeyLongPress(code, event)) {
			return true;
		}
		return super.onKeyLongPress(code, event);
	}

	public void setKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public KeyListener getKeyListener() {
		return keyListener;
	}
}
