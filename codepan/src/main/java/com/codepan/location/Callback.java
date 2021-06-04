package com.codepan.location;

import android.location.Location;

public class Callback {

	public interface OnLocationAverageChangedCallback {
		void onLocationAverageChanged(Location average);
	}
}
