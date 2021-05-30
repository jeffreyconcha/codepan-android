package com.codepan.widget.media.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.codepan.R;
import com.codepan.app.CPFragment;
import com.codepan.utils.CodePanUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CPFullscreenFragment extends CPFragment {

	private CPVideoView video;
	private ViewGroup parent;
	private LayoutParams lp;

	@Override
	public void onStart() {
		super.onStart();
		CodePanUtils.changeOrientation(activity, true);
		CodePanUtils.enableFullscreen(activity);
		if(video != null) {
			video.setFullscreen(true);
			FrameLayout view = (FrameLayout) getView();
			lp = video.getLayoutParams();
			parent = (ViewGroup) video.getParent();
			parent.removeView(video);
			LayoutParams lp = view.getLayoutParams();
			video.setLayoutParams(lp);
			view.addView(video);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		video.setFullscreen(false);
		CodePanUtils.changeOrientation(activity, false);
		CodePanUtils.disableFullscreen(activity);
		if(video != null) {
			FrameLayout view = (FrameLayout) getView();
			view.removeView(video);
			video.setLayoutParams(lp);
			parent.addView(video);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.video_fullscreen_container_layout,
				container, false);
	}

	public void setVideo(CPVideoView video) {
		this.video = video;
	}
}
