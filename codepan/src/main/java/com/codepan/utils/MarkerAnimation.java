/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */

package com.codepan.utils;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerAnimation {

	private static final long DELAY = 1000L;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void animateMarkerToICS(Marker marker, LatLng position, final LatLngInterpolator interpolator) {
		TypeEvaluator<LatLng> evaluator = new TypeEvaluator<LatLng>() {
			@Override
			public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
				return interpolator.interpolate(fraction, startValue, endValue);
			}
		};
		Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
		ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, evaluator, position);
		animator.setDuration(DELAY);
		animator.start();
	}

	public interface LatLngInterpolator {
		LatLng interpolate(float fraction, LatLng a, LatLng b);

		class LinearFixed implements LatLngInterpolator {
			@Override
			public LatLng interpolate(float fraction, LatLng a, LatLng b) {
				double lat = (b.latitude - a.latitude) * fraction + a.latitude;
				double lngDelta = b.longitude - a.longitude;
				if(Math.abs(lngDelta) > 180) {
					lngDelta -= Math.signum(lngDelta) * 360;
				}
				double lng = lngDelta * fraction + a.longitude;
				return new LatLng(lat, lng);
			}
		}
	}
}