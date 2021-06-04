package com.codepan.example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		findViewById(R.id.btnCalendar).setOnClickListener(view -> {
			CalendarFragment calendar = new CalendarFragment();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, calendar);
			transaction.addToBackStack(null);
			transaction.commit();
		});
		findViewById(R.id.btnTable).setOnClickListener(view -> {
			TableFragment table = new TableFragment();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, table);
			transaction.addToBackStack(null);
			transaction.commit();
		});
	}
}
