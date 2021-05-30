package com.codepan.widget.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.VideoView;

import com.codepan.R;
import com.codepan.callback.Interface.OnCompletionCallback;
import com.codepan.callback.Interface.OnProgressCallback;
import com.codepan.widget.media.callback.Interface.OnFullscreenCallback;
import com.codepan.widget.media.callback.Interface.OnMediaInterruptedCallback;
import com.codepan.widget.media.callback.Interface.OnSkipNextCallback;
import com.codepan.widget.media.callback.Interface.OnSkipPreviousCallback;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;

import java.util.HashMap;

public class CPVideoView extends FrameLayout implements OnSeekBarChangeListener,
		OnPreparedListener, OnCompletionListener, OnErrorListener {

	private final long INTERVAL = 1000L;
	private final long CONTROL_DURATION = 5000L;

	private CodePanButton btnPlayVideo, btnNextVideo, btnPreviousVideo, btnFullScreenVideo;
	private boolean isInitialized, hasError, hasNext, hasPrevious, isBufferHidden,
			isSeekDisabled, isAutoPlayEnabled, isFirstPlayDone, isNavigationHidden,
			hasInitial, isViewCreated, isFullscreen;
	private CodePanLabel tvProgressVideo, tvDurationVideo, tvErrorVideo, tvTitleVideo;
	private ImageView ivPlayVideo, ivNextVideo, ivPreviousVideo, ivLoadingVideo;
	private RelativeLayout rlControllerVideo, rlContentVideo;
	private OnMediaInterruptedCallback mediaInterruptedCallback;
	private OnSkipPreviousCallback previousCallback;
	private OnFullscreenCallback fullscreenCallback;
	private OnCompletionCallback completionCallback;
	private OnProgressCallback progressCallback;
	private MediaMetadataRetriever retriever;
	private OnSkipNextCallback nextCallback;
	private ImageView ivContentVideo;
	private int progress, initial, max;
	private LinearLayout llPlayVideo;
	private VideoView vvContentVideo;
	private SeekBar sbProgressVideo;
	private LayoutInflater inflater;
	private String url, title;
	private Bitmap thumbnail;
	private Animation anim;
	private long elapsed;

	public CPVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflater = LayoutInflater.from(context);
		anim = AnimationUtils.loadAnimation(context,
				R.anim.rotate_clockwise);
		retriever = new MediaMetadataRetriever();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(!isViewCreated) {
			View view = inflater.inflate(R.layout.video_normal_layout, this, false);
			btnFullScreenVideo = view.findViewById(R.id.btnFullScreenVideo);
			rlControllerVideo = view.findViewById(R.id.rlControllerVideo);
			btnPreviousVideo = view.findViewById(R.id.btnPreviousVideo);
			sbProgressVideo = view.findViewById(R.id.sbProgressVideo);
			tvProgressVideo = view.findViewById(R.id.tvProgressVideo);
			tvDurationVideo = view.findViewById(R.id.tvDurationVideo);
			ivPreviousVideo = view.findViewById(R.id.ivPreviousVideo);
			ivLoadingVideo = view.findViewById(R.id.ivLoadingVideo);
			vvContentVideo = view.findViewById(R.id.vvContentVideo);
			rlContentVideo = view.findViewById(R.id.rlContentVideo);
			ivContentVideo = view.findViewById(R.id.ivContentVideo);
			tvTitleVideo = view.findViewById(R.id.tvTitleVideo);
			tvErrorVideo = view.findViewById(R.id.tvErrorVideo);
			btnPlayVideo = view.findViewById(R.id.btnPlayVideo);
			btnNextVideo = view.findViewById(R.id.btnNextVideo);
			ivPlayVideo = view.findViewById(R.id.ivPlayVideo);
			ivNextVideo = view.findViewById(R.id.ivNextVideo);
			llPlayVideo = view.findViewById(R.id.llPlayVideo);
			sbProgressVideo.setOnSeekBarChangeListener(this);
			vvContentVideo.setOnPreparedListener(this);
			vvContentVideo.setOnCompletionListener(this);
			vvContentVideo.setOnErrorListener(this);
			if(hasInitial) {
				setDuration(max);
				setProgress(initial);
				if(thumbnail != null) {
					ivContentVideo.setImageBitmap(thumbnail);
					ivContentVideo.setVisibility(View.VISIBLE);
				}
			}
			if(isSeekDisabled) {
				sbProgressVideo.setEnabled(false);
			}
			btnPlayVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					doPlayPause();
				}
			});
			rlContentVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(!hasError) {
						if(!isControllerVisible()) {
							CodePanUtils.fadeIn(rlControllerVideo);
							updatePlayStatus();
							if(!isInitialized && isAutoPlayEnabled) {
								llPlayVideo.setVisibility(View.GONE);
							}
						}
						else {
							hideController();
							elapsed = 0L;
						}
					}
				}
			});
			tvErrorVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					hasError = (false);
					loadVideo(url, title);
				}
			});
			btnNextVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(nextCallback != null) {
						nextCallback.onSkipNext(CPVideoView.this);
					}
				}
			});
			btnPreviousVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(previousCallback != null) {
						previousCallback.onSkipPrevious(CPVideoView.this);
					}
				}
			});
			btnFullScreenVideo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(fullscreenCallback != null) {
						fullscreenCallback.onFullScreen(CPVideoView.this);
					}
				}
			});
			if(isAutoPlayEnabled) {
				loadVideo(url, title);
				isFirstPlayDone = true;
			}
			updatePreviousStatus(hasPrevious);
			updateNextStatus(hasNext);
			addView(view);
			isViewCreated = true;
		}
		else {
			if(progress != 0 && isInitialized) {
				vvContentVideo.seekTo(progress);
				isFirstPlayDone = false;
				setLoading(true);
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		prepareToInterrupt();
		if(mediaInterruptedCallback != null) {
			mediaInterruptedCallback.onPlayInterrupted(progress, max, thumbnail);
		}
	}

	private void initState(String url) {
		boolean isNetwork = URLUtil.isNetworkUrl(url);
		hasError = (false);
		setLoading(isNetwork);
		updateErrorStatus();
		setDuration(max);
		setProgress(isInitialized ? progress : initial);
		setBuffer(0, 0);
	}

	public void loadVideo(String url, String title) {
		if(url != null && !url.isEmpty()) {
			this.progress = 0;
			this.url = url;
			initState(url);
			Uri uri = Uri.parse(url);
			vvContentVideo.stopPlayback();
			vvContentVideo.setVisibility(View.GONE);
			vvContentVideo.setVisibility(View.VISIBLE);
			vvContentVideo.setVideoURI(uri);
			updatePreviousStatus(hasPrevious);
			updateNextStatus(hasNext);
		}
		updateTitle(title);
	}

	@Override
	public void onProgressChanged(SeekBar bar, int progress, boolean isTouched) {
		this.progress = progress;
		if(isTouched) {
			vvContentVideo.seekTo(progress);
			if(max != 0) {
				setProgress(progress);
				this.initial = progress;
			}
		}
		if(progressCallback != null) {
			progressCallback.onProgress(progress, max);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		CodePanUtils.fadeOut(ivContentVideo);
		this.isInitialized = true;
		this.hasError = false;
		this.elapsed = SystemClock.elapsedRealtime();
		setDuration(mp.getDuration());
		if(hasInitial) {
			int difference = max - initial;
			if(difference > 1000) {
				vvContentVideo.seekTo(initial);
			}
			hasInitial = false;
		}
		else {
			if(isAutoPlayEnabled) {
				doPlayPause();
			}
		}
		if(!isFirstPlayDone) {
			play();
		}
		else {
			updatePlayStatus();
		}
		centerInside(vvContentVideo, mp);
		setLoading(false);
		loadThumbnail();
	}

	public void doPlayPause() {
		if(!vvContentVideo.isPlaying()) {
			if(isFirstPlayDone) {
				play();
			}
			else {
				loadVideo(url, title);
			}
		}
		else {
			pause();
		}
		updatePlayStatus();
	}

	public void pause() {
		if(isInitialized && vvContentVideo.isPlaying()) {
			vvContentVideo.pause();
			updatePlayStatus();
		}
	}

	public void play() {
		if(isInitialized && !vvContentVideo.isPlaying()) {
			vvContentVideo.start();
			sbProgressVideo.removeCallbacks(update);
			sbProgressVideo.postDelayed(update, INTERVAL);
			isFirstPlayDone = true;
			updatePlayStatus();
		}
	}

	private void loadThumbnail() {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(url != null) {
						if(url.contains("file://")) {
							retriever.setDataSource(getContext(), Uri.parse(url));
						}
						else {
							retriever.setDataSource(url, new HashMap<String, String>());
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int i, int i1) {
		this.hasError = true;
		setLoading(false);
		updateErrorStatus();
		return true;
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {
			if(vvContentVideo.isPlaying()) {
				int max = sbProgressVideo.getMax();
				int buffer = vvContentVideo.getBufferPercentage();
				int progress = vvContentVideo.getCurrentPosition();
				setProgress(progress);
				if(!isBufferHidden) {
					setBuffer(max, buffer);
				}
				if(isControllerVisible()) {
					long current = SystemClock.elapsedRealtime();
					long duration = current - elapsed;
					if(duration >= CONTROL_DURATION) {
						hideController();
						elapsed = SystemClock.elapsedRealtime();
					}
				}
				else {
					elapsed = SystemClock.elapsedRealtime();
				}
				sbProgressVideo.postDelayed(this, INTERVAL);
			}
		}
	};

	private void centerInside(VideoView view, MediaPlayer mp) {
		ViewGroup parent = (ViewGroup) view.getParent();
		int w = mp.getVideoWidth();
		int h = mp.getVideoHeight();
		float ratio = (float) w / (float) h;
		int mw = parent.getLayoutParams().width;
		int mh = parent.getLayoutParams().height;
		int height = (int) ((float) mw / ratio);
		int width = (int) ((float) mh * ratio);
		if(height < mh) {
			view.getLayoutParams().height = height;
		}
		else {
			view.getLayoutParams().width = width;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOnSkipPreviousCallback(OnSkipPreviousCallback previousCallback) {
		this.previousCallback = previousCallback;
	}

	public void setOnSkipNextCallback(OnSkipNextCallback nextCallback) {
		this.nextCallback = nextCallback;
	}

	public void setOnFullscreenCallback(OnFullscreenCallback fullscreenCallback) {
		this.fullscreenCallback = fullscreenCallback;
	}

	public int getDuration() {
		return sbProgressVideo.getMax();
	}

	public boolean isPlaying() {
		return vvContentVideo.isPlaying();
	}

	private void setDuration(int duration) {
		this.max = duration;
		String formatted = CodePanUtils.millisToDuration(duration);
		tvDurationVideo.setText(formatted);
		sbProgressVideo.setMax(duration);
	}

	private void setProgress(int progress) {
		this.progress = progress;
		sbProgressVideo.setProgress(progress);
		String formatted = CodePanUtils.millisToDuration(progress);
		tvProgressVideo.setText(formatted);
	}

	private void setBuffer(int max, int buffer) {
		float percentage = (float) (buffer * max) / 100F;
		sbProgressVideo.setSecondaryProgress((int) percentage);
	}

	private void updatePlayStatus() {
		boolean isPlaying = isPlaying();
		ivPlayVideo.setEnabled(!isPlaying);
	}

	private void updateErrorStatus() {
		if(hasError) {
			tvErrorVideo.setVisibility(View.VISIBLE);
			rlControllerVideo.setVisibility(View.VISIBLE);
			llPlayVideo.setVisibility(View.GONE);
		}
		else {
			tvErrorVideo.setVisibility(View.GONE);
		}
	}

	private void setLoading(boolean isLoading) {
		if(isLoading) {
			llPlayVideo.setVisibility(View.GONE);
			ivLoadingVideo.setVisibility(View.VISIBLE);
			ivLoadingVideo.startAnimation(anim);
		}
		else {
			ivLoadingVideo.setVisibility(View.GONE);
			ivLoadingVideo.clearAnimation();
			if(!hasError) {
				llPlayVideo.setVisibility(View.VISIBLE);
			}
		}
	}

	private boolean isControllerVisible() {
		return rlControllerVideo.getVisibility() == View.VISIBLE;
	}

	private void hideController() {
		CodePanUtils.fadeOut(rlControllerVideo);
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public void setHasPrevious(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	private void updateNextStatus(boolean hasNext) {
		btnNextVideo.setEnabled(hasNext);
		if(isNavigationHidden) {
			ivNextVideo.setVisibility(View.GONE);
		}
		else {
			ivNextVideo.setEnabled(hasNext);
		}
	}

	private void updatePreviousStatus(boolean hasPrevious) {
		btnPreviousVideo.setEnabled(hasPrevious);
		if(isNavigationHidden) {
			ivPreviousVideo.setVisibility(View.GONE);
		}
		else {
			ivPreviousVideo.setEnabled(hasPrevious);
		}
	}

	private void updateTitle(String title) {
		tvTitleVideo.setText(title);
	}

	public int getProgress() {
		int current = 0;
		if(vvContentVideo != null) {
			current = vvContentVideo.getCurrentPosition();
		}
		return current;
	}

	public void setInitial(int initial, int max, Bitmap thumbnail) {
		this.hasInitial = true;
		this.initial = initial;
		this.thumbnail = thumbnail;
		this.max = max;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(hasNext) {
			btnNextVideo.callOnClick();
		}
		if(completionCallback != null) {
			completionCallback.onCompletion();
		}
		updatePlayStatus();
	}

	public void hideBuffer(boolean isBufferHidden) {
		this.isBufferHidden = isBufferHidden;
	}

	public String getUrl() {
		return this.url;
	}

	public void setSeekDisabled(boolean isSeekDisabled) {
		this.isSeekDisabled = isSeekDisabled;
	}

	public boolean isSeekDisabled() {
		return this.isSeekDisabled;
	}

	public void setAutoPlayEnabled(boolean isAutoPlayEnabled) {
		this.isAutoPlayEnabled = isAutoPlayEnabled;
	}

	public void setNavigationHidden(boolean isNavigationHidden) {
		this.isNavigationHidden = isNavigationHidden;
	}

	public void setOnProgressCallback(OnProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}

	public void setOnCompletionCallback(OnCompletionCallback completionCallback) {
		this.completionCallback = completionCallback;
	}

	public void setOnMediaInterruptedCallback(OnMediaInterruptedCallback mediaInterruptedCallback) {
		this.mediaInterruptedCallback = mediaInterruptedCallback;
	}

	public boolean isFullscreen() {
		return isFullscreen;
	}

	public void setFullscreen(boolean isFullscreen) {
		this.isFullscreen = isFullscreen;
	}

	public void prepareToInterrupt() {
		if(vvContentVideo != null) {
//			this.progress = vvContentVideo.getCurrentPosition();
//			this.max = vvContentVideo.getDuration();
			Bitmap bitmap = retriever.getFrameAtTime(progress,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			if(bitmap != null) {
				int w = vvContentVideo.getWidth();
				int h = vvContentVideo.getHeight();
				this.thumbnail = CodePanUtils.resizeBitmap(bitmap, w, h);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			if(isFirstPlayDone) {
				vvContentVideo.seekTo(progress);
				setLoading(true);
			}
		}
		else {
			isInitialized = false;
			if(vvContentVideo.isPlaying()) {
				vvContentVideo.pause();
			}
		}
	}

	public void hide() {
		if(vvContentVideo != null) {
			vvContentVideo.setVisibility(View.INVISIBLE);
			if(isInitialized) {
				progress = sbProgressVideo.getProgress();
			}
		}
	}

	public void show() {
		if(vvContentVideo != null) {
			vvContentVideo.setVisibility(View.VISIBLE);
			if(isInitialized) {
				vvContentVideo.seekTo(progress);
			}
		}
	}
}
