package com.codepan.model;

import android.location.Location;
public class GpsData {

	public String ID;
	public String date;
	public String time;
	public float speed;
	public float bearing;
	public long millis;
	public float accuracy;
	public double longitude;
	public double latitude;
	public double altitude;
	public boolean isValid;
	public boolean isIndoor;
	public boolean isEnabled;
	public boolean withHistory;
	public Location location;

	public void optDate(String date) {
		if(date != null) {
			this.date = date;
		}
		else {
			this.date = "0000-00-00";
		}
	}

	public void optTime(String time) {
		if(time != null) {
			this.time = time;
		}
		else {
			this.time = "00:00:00";
		}
	}
}
