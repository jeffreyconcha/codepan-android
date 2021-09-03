package com.codepan.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.utils.Console;
import com.codepan.widget.timepicker.view.TimePickerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class TimePickerSample extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.time_picker_sample_layout, container, false);
		TimePickerView tpvTimePicker = view.findViewById(R.id.tpvTimePicker);
		tpvTimePicker.setDefaultTime("03:00:00");
		tpvTimePicker.setOnCancelCallback(() -> {
			FragmentManager manager = getFragmentManager();
			if (manager != null) {
				manager.popBackStack();
			}
		});
		tpvTimePicker.setOnPickTimeCallback(time -> {
			Console.log("Selected Time:" + time);
		});
		return view;
	}
}
