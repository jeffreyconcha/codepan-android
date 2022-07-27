package com.codepan.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

@SuppressLint("MissingPermission")
public abstract class LocationListener implements android.location.LocationListener,
	GpsStatus.Listener, Runnable {

	enum LocationCriteria {
		HIGH,
		LOW,
	}

	private final long SENSOR_DELAY = 2000L;
	private final long SENSOR_ALLOWANCE = 3000L;
	private final float HIGH_SNR = 21F;
	private final float LOW_SNR = 13F;
	private final float SENSITIVITY = 0.1F;
	private long motionUpdate, sensorUpdate;
	private GnssStatus.Callback callback;
	private int used, visible, snr;
	private float x, y, z, minSnr;
	private boolean requestSensor;
	private LocationManager lm;
	private SensorManager sm;
	private Handler handler;
	private Sensor sensor;

	public LocationListener(Context context, LocationCriteria criteria) {
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		handler = new Handler();
		switch(criteria) {
			case HIGH:
				minSnr = HIGH_SNR;
				break;
			case LOW:
				minSnr = LOW_SNR;
				break;
		}
	}

	public void startLocationUpdates() {
		if(lm != null) {
			try {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					0, 0, this);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					callback = new GnssStatus.Callback() {
						@Override
						public void onSatelliteStatusChanged(GnssStatus status) {
							super.onSatelliteStatusChanged(status);
							int snr = 0;
							int used = 0;
							int visible = status.getSatelliteCount();
							for(int i = 0; i < visible; i++) {
								if(status.usedInFix(i)) {
									float decibel = status.getCn0DbHz(i);
									if(decibel >= minSnr) {
										snr++;
									}
									used++;
								}
							}
							updateSatelliteCount(used, visible, snr);
						}
					};
					lm.registerGnssStatusCallback(callback);
				}
				else {
					lm.addGpsStatusListener(this);
				}
				requestSensor = true;
				handler.postDelayed(this, SENSOR_DELAY);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	public void stopLocationUpdates() {
		if(lm != null) {
			try {
				lm.removeUpdates(this);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					lm.unregisterGnssStatusCallback(callback);
				}
				else {
					lm.removeGpsStatusListener(this);
				}
				requestSensor = false;
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	protected abstract void onLocationChanged(Location location, int used, int visible, int snr);

	@Override
	public void onGpsStatusChanged(int event) {
		if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			GpsStatus status = lm.getGpsStatus(null);
			int snr = 0;
			int used = 0;
			int visible = 0;
			for(GpsSatellite satellite : status.getSatellites()) {
				if(satellite.usedInFix()) {
					float decibel = satellite.getSnr();
					if(decibel >= minSnr) {
						snr++;
					}
					used++;
				}
				visible++;
			}
			updateSatelliteCount(used, visible, snr);
		}
	}

	private void updateSatelliteCount(int used, int visible, int snr) {
		this.snr = snr;
		this.used = used;
		this.visible = visible;
	}

	@Override
	public void onLocationChanged(Location location) {
		onLocationChanged(location, used, visible, snr);
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
	}

	@Override
	public void onProviderEnabled(String s) {
	}

	@Override
	public void onProviderDisabled(String s) {
	}

	@Override
	public void run() {
		handler.removeCallbacks(this);
		sm.registerListener(new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				sm.unregisterListener(this, sensor);
				long elapsed = SystemClock.elapsedRealtime();
				float cx = event.values[0];
				float cy = event.values[1];
				float cz = event.values[2];
				float dx = Math.abs(cx - x);
				float dy = Math.abs(cy - y);
				float dz = Math.abs(cz - z);
				if(dx >= SENSITIVITY || dy >= SENSITIVITY || dz >= SENSITIVITY) {
					motionUpdate = elapsed;
				}
				x = cx;
				y = cy;
				z = cz;
				sensorUpdate = elapsed;
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int i) {
			}
		}, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		if(requestSensor) {
			handler.postDelayed(this, SENSOR_DELAY);
		}
	}

	protected boolean isSensorActive() {
		long elapsed = SystemClock.elapsedRealtime();
		long difference = elapsed - sensorUpdate;
		return difference <= SENSOR_ALLOWANCE;
	}

	protected boolean isMoving() {
		long elapsed = SystemClock.elapsedRealtime();
		long difference = elapsed - motionUpdate;
		return difference <= SENSOR_ALLOWANCE;
	}
}
