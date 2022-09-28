package com.codepan.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.codepan.location.Callback.OnLocationAverageChangedCallback;
import com.codepan.utils.CodePanUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

public class LocationOptimizer extends LocationListener {

	public static final float NETWORK_STABILITY_RADIUS = 20F;
	public static final float NETWORK_GEOFENCE_RADIUS = 200F;
	public static final float GPS_GEOFENCE_RADIUS = 20F;
	private final int HIGH_GPS_LOCK_REQUIREMENTS = 5;
	private final int HIGH_GPS_FIX_REQUIREMENTS = 5;
	private final int HIGH_GPS_SNR_REQUIREMENTS = 4;
	private final int LOW_GPS_LOCK_REQUIREMENTS = 3;
	private final int LOW_GPS_FIX_REQUIREMENTS = 3;
	private final int LOW_GPS_SNR_REQUIREMENTS = 3;
	private final int MAX_AVERAGE_DATA_STRICT = 60;
	private final int MAX_AVERAGE_DATA_NORMAL = 15;
	private final int MAX_OUTSIDE_DATA = 30;
	private final int MAX_RANDOM_METER = 5;
	private final int MAX_DEGREE = 360;

	private ArrayList<Location> locationList = new ArrayList<>();
	private ArrayList<Location> candidatelist = new ArrayList<>();
	private OnLocationAverageChangedCallback callback;
	private int counter, lock, minLock, minFix, minSnr;
	private boolean initAverage = true;
	private Location outdoor;
	private Location network;
	private Location average;
	private Location recent;
	private Random random;
	private Location gps;
	private long interval;

	public LocationOptimizer(Context context, long interval, LocationCriteria criteria) {
		super(context, criteria);
		this.interval = interval;
		this.random = new Random();
		switch(criteria) {
			case HIGH:
				minLock = HIGH_GPS_LOCK_REQUIREMENTS;
				minFix = HIGH_GPS_FIX_REQUIREMENTS;
				minSnr = HIGH_GPS_SNR_REQUIREMENTS;
				break;
			case LOW:
				minLock = LOW_GPS_LOCK_REQUIREMENTS;
				minFix = LOW_GPS_FIX_REQUIREMENTS;
				minSnr = LOW_GPS_SNR_REQUIREMENTS;
				break;
		}
	}

	public void inputData(Location location) {
		float distance = recent != null ? recent.distanceTo(location) : 0F;
		this.recent = identifyLocation(location);
		int size = locationList.size();
		if(size < MAX_AVERAGE_DATA_NORMAL && initAverage) {
			String provider = recent.getProvider();
			if(!provider.equals(LocationManager.GPS_PROVIDER)) {
				if(distance > NETWORK_STABILITY_RADIUS) {
					locationList.clear();
				}
			}
			locationList.add(recent);
			int actual = locationList.size();
			if(actual == MAX_AVERAGE_DATA_NORMAL) {
				computeAverage();
			}
		}
	}

	private void computeAverage() {
		float accuracy = 0F;
		double latitude = 0D;
		double longitude = 0D;
		int actual = locationList.size();
		for(Location location : locationList) {
			accuracy += location.getAccuracy();
			latitude += location.getLatitude();
			longitude += location.getLongitude();
		}
		accuracy /= actual;
		latitude /= actual;
		longitude /= actual;
		long time = System.currentTimeMillis();
		average = new Location(LocationManager.GPS_PROVIDER);
		average.setTime(time);
		average.setAccuracy(accuracy);
		average.setLatitude(latitude);
		average.setLongitude(longitude);
		if(callback != null) {
			callback.onLocationAverageChanged(average);
		}
	}

	public Location getLocation() {
		if(recent != null && average != null) {
			float distance = average.distanceTo(recent);
			String provider = recent.getProvider();
			if(!provider.equals(LocationManager.GPS_PROVIDER)) {
				if(distance <= NETWORK_GEOFENCE_RADIUS) {
					double latitude = average.getLatitude();
					double longitude = average.getLongitude();
					int bearing = random.nextInt(MAX_DEGREE);
					int meters = random.nextInt(MAX_RANDOM_METER);
					LatLng point = CodePanUtils.travel(latitude,
						longitude, bearing, meters);
					recent.setAccuracy(average.getAccuracy());
					recent.setLatitude(point.latitude);
					recent.setLongitude(point.longitude);
					recent.setProvider("average");
					resetNetworkCounter();
					candidatelist.clear();
				}
				else {
					if(counter >= MAX_OUTSIDE_DATA) {
						int max = outdoor != null && outdoor.distanceTo(recent) < NETWORK_GEOFENCE_RADIUS ?
							MAX_AVERAGE_DATA_NORMAL : MAX_AVERAGE_DATA_STRICT;
						candidatelist.add(recent);
						if(candidatelist.size() >= max) {
							locationList.clear();
							locationList.addAll(candidatelist);
							computeAverage();
							resetNetworkCounter();
							candidatelist.clear();
						}
					}
					else {
						if(network != null) {
							float d = network.distanceTo(recent);
							if(d <= NETWORK_STABILITY_RADIUS) {
								outsideNetworkRadius();
							}
							else {
								resetNetworkCounter();
							}
						}
						network = recent;
					}
					return null;
				}
			}
			else {
				if(distance > GPS_GEOFENCE_RADIUS && isGpsLock()) {
					this.average = recent;
					if(callback != null) {
						callback.onLocationAverageChanged(average);
					}
					resetGpsLock();
					resetNetworkCounter();
					candidatelist.clear();
				}
			}
		}
		return recent;
	}

	private Location identifyLocation(Location location) {
		if(gps != null) {
			long time = gps.getTime() - location.getTime();
			long difference = Math.abs(time);
			if(difference < interval && (isMoving() || !isSensorActive())) {
				location.setProvider(LocationManager.GPS_PROVIDER);
				watchGpsLock();
				if(isGpsLock()) {
					this.outdoor = gps;
				}
			}
			else {
				resetGpsLock();
			}
		}
		return location;
	}

	@Override
	protected void onLocationChanged(Location location, int used, int visible, int snr) {
		if(location != null) {
			String provider = location.getProvider();
			if(provider.equals(LocationManager.GPS_PROVIDER)) {
				if(used >= minFix && snr >= minSnr) {
					this.gps = location;
				}
			}
		}
	}

	public void setOnLocationAverageChangedCallback(OnLocationAverageChangedCallback callback) {
		this.callback = callback;
	}

	public void setLastAverage(Location average) {
		if(average != null) {
			this.average = average;
			this.initAverage = false;
		}
	}

	private void resetNetworkCounter() {
		this.counter = 0;
	}

	private void outsideNetworkRadius() {
		this.counter += 1;
	}

	private void resetGpsLock() {
		this.lock = 0;
	}

	private void watchGpsLock() {
		if(lock < minLock) {
			lock += 1;
		}
	}

	private boolean isGpsLock() {
		return lock >= minLock;
	}

	public void setInitAverage(boolean initAverage) {
		this.initAverage = initAverage;
	}

	@Override
	public void stopLocationUpdates() {
		super.stopLocationUpdates();
		this.initAverage = true;
		this.average = null;
		this.recent = null;
		this.network = null;
		this.gps = null;
		locationList.clear();
		candidatelist.clear();
		resetNetworkCounter();
		resetGpsLock();
	}
}
