package com.codepan.widget.media.callback;

import android.graphics.Bitmap;

import com.codepan.widget.media.view.CPVideoView;

public class Interface {

	public interface OnSkipNextCallback {
		void onSkipNext(CPVideoView video);
	}

	public interface OnSkipPreviousCallback {
		void onSkipPrevious(CPVideoView video);
	}

	public interface OnFullscreenCallback {
		void onFullScreen(CPVideoView video);
	}

	public interface OnMediaInterruptedCallback {
		void onPlayInterrupted(int progress, int max, Bitmap thumbnail);
	}
}
