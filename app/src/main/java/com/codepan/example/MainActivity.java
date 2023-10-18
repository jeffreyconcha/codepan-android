package com.codepan.example;

import android.os.Bundle;
import android.os.Looper;

import com.codepan.app.CPFragmentActivity;
import com.codepan.callback.Interface;
import com.codepan.net.Authorization;
import com.codepan.net.Do;
import com.codepan.test.Test;
import com.codepan.utils.Debouncer;
import com.codepan.widget.CodePanTextField;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends CPFragmentActivity {

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
				String url = "https://api.app.lernnex.com/api/lesson/get-student-lesson-content/351";
//				String url = "https://www.officialgazette.gov.ph/";
//				String url = "https://catfact.ninja/fact";
				JSONObject json = new JSONObject();
				json.put("user_id", 4202);
				json.put("library_party_id", 8623);
				json.put("module_resource_id", 350);
				Authorization auth = new Authorization();
				auth.setToken("171|Q69B747gj14qmgpoP8UqCOIXL1OtH7UfHJvsVg6G");
				auth.setType(Authorization.BEARER);
//				json.put("api_key", "V0gu1964h5j762s7WiG52i45CMg1s9Xo8dbX565P20m3w7U7CA");
				Do.httpGet(url, json, auth, true, 0);
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
