package com.codepan.example;

import android.os.Bundle;

import com.codepan.widget.CodePanButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		CodePanButton btnCalendar = findViewById(R.id.btnCalendar);
		btnCalendar.setOnClickListener(view -> {
			CalendarFragment calendar = new CalendarFragment();
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, calendar);
			transaction.addToBackStack(null);
			transaction.commit();
		});
	}
}
