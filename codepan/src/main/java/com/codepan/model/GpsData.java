package com.codepan.model;

import android.location.Location;

import com.codepan.time.DateTime;
import com.codepan.time.DateTimeFields;

import org.jetbrains.annotations.NotNull;

public class GpsData implements DateTimeFields {
	public String ID;
	public float speed;
	public float bearing;
	public float accuracy;
	public double longitude;
	public double latitude;
	public double altitude;
	public boolean isValid;
	public boolean isIndoor;
	public boolean isEnabled;
	public boolean withHistory;
	public Location location;
	public DateTime dt;

	@NotNull
	@Override
	public String getDate() {
		return getDateTime().getDate();
	}

	@NotNull
	@Override
	public String getTime() {
		return getDateTime().getTime();
	}

	@Override
	public long getTimestamp() {
		return getDateTime().getTimestamp();
	}

	@NotNull
	@Override
	public DateTime getDateTime() {
		if (dt == null) {
			dt = new DateTime();
		}
		return dt;
	}
}
