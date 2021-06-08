package com.codepan.example;

import android.os.Bundle;

import com.codepan.test.Test;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Test.run(this);
		findViewById(R.id.btnCalendar).setOnClickListener(view -> {
			CalendarSample dialog = new CalendarSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, dialog);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnTimePicker).setOnClickListener(view -> {
			TimePickerSample dialog = new TimePickerSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, dialog);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnTable).setOnClickListener(view -> {
			TableSample table = new TableSample();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, table);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnCamera).setOnClickListener(view -> {
			CameraFragment camera = new CameraFragment();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, camera);
			transaction.addToBackStack(null);
			transaction.commit();
		});
	}
}
