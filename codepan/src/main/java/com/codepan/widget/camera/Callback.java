package com.codepan.widget.camera;

public class Callback {
	public interface OnCaptureCallback {
		void onCapture(String fileName);
	}

	public interface OnCameraErrorCallback {
		void onCameraError();
	}
}
