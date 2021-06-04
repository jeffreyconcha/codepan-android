package com.codepan.widget.wheel;

public class Callback {
	public interface OnWheelStopCallback {
		void onWheelStop(float degree);
	}

	public interface OnWheelSpinningCallback {
		void onWheelSpinning(float degree);
	}
}
