package com.codepan.example;

import android.os.Bundle;
import android.os.Looper;

import com.codepan.app.CPFragmentActivity;
import com.codepan.callback.Interface;
import com.codepan.net.HttpRequest;
import com.codepan.test.Test;
import com.codepan.utils.Console;
import com.codepan.utils.Debouncer;
import com.codepan.widget.CodePanTextField;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends CPFragmentActivity {

	@OptIn(markerClass = ExperimentalGetImage.class)
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Test.run(this);
		findViewById(R.id.btnCalendar).setOnClickListener(view -> {
			CalendarSample dialog = new CalendarSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.rlMain, dialog);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnTimePicker).setOnClickListener(view -> {
			TimePickerSample dialog = new TimePickerSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.rlMain, dialog);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnTable).setOnClickListener(view -> {
			TableSample table = new TableSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.rlMain, table);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnCamera).setOnClickListener(view -> {
			CameraXFragment camera = new CameraXFragment();
//			CameraFragment camera = new CameraFragment();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.rlMain, camera);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		CodePanTextField tf = findViewById(R.id.etNumeric);
		tf.setOnTextChangedCallback(new Debouncer<>(data -> {
			requestHttp();
		}));
	}

	private void requestHttp() {
		Thread bg = new Thread(() -> {
			Looper.prepare();
			try {
				String url = "https://www.app.tarkie.com/API/3.0/get-company";
				String response = new HttpRequest(this, url, null, null, 1000).get(null, true);
				Console.logResponse(response);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		});
		bg.start();
	}

	@Override
	public void onLoadSplash(Interface.OnInitializeCallback initializeCallback) {
	}
}
