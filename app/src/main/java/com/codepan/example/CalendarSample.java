package com.codepan.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.utils.Console;
import com.codepan.widget.calendar.view.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CalendarSample extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_sample_layout, container, false);
		CalendarView cvCalendar = view.findViewById(R.id.cvCalendar);
		cvCalendar.setOnCancelCallback(() -> {
			FragmentManager manager = getFragmentManager();
			if (manager != null) {
				manager.popBackStack();
			}
		});
		cvCalendar.setOnPickDateCallback(date -> {
			Console.log("Selected Date: " + date);
		});
		return view;
	}
}
