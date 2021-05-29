package com.codepan.media.view;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.codepan.R;
import com.codepan.callback.Interface.OnCompletionCallback;
import com.codepan.callback.Interface.OnProgressCallback;
import com.codepan.callback.Interface.OnResultCallback;
import com.codepan.media.callback.Interface.OnMediaInterruptedCallback;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;

public class CPAudioView extends FrameLayout implements OnPreparedListener, OnErrorListener,
		OnCompletionListener, OnBufferingUpdateListener, OnSeekBarChangeListener {

	private final long DELAY = 1000L;
	private OnMediaInterruptedCallback mediaInterruptedCallback;
	private boolean isInitialized, hasInitial, isViewCreated,
			isBufferHidden;
	private OnCompletionCallback completionCallback;
	private OnProgressCallback progressCallback;
	private OnResultCallback resultCallback;
	private CodePanLabel tvDurationAudio;
	private CodePanButton btnPlayAudio;
	private int current, max, initial;
	private ImageView ivLoadingAudio;
	private SeekBar sbProgressAudio;
	private LayoutInflater inflater;
	private ImageView ivPlayAudio;
	private String duration;
	private Handler handler;
	private Animation anim;
	private MediaPlayer mp;
	private String url;

	public CPAudioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflater = LayoutInflater.from(context);
		anim = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise);
		handler = new Handler();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(!isViewCreated) {
			View view = inflater.inflate(R.layout.audio_layout, this, false);
			ivPlayAudio = view.findViewById(R.id.ivPlayAudio);
			btnPlayAudio = view.findViewById(R.id.btnPlayAudio);
			ivLoadingAudio = view.findViewById(R.id.ivLoadingAudio);
			tvDurationAudio = view.findViewById(R.id.tvDurationAudio);
			sbProgressAudio = view.findViewById(R.id.sbProgressAudio);
			sbProgressAudio.setOnSeekBarChangeListener(this);
			btnPlayAudio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mp != null) {
						if(mp.isPlaying()) {
							pause();
						}
						else {
							play();
						}
					}
					else {
						loadAudio(url);
					}
				}
			});
			setProgress();
			addView(view);
			isViewCreated = true;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(isInitialized) {
			if(mediaInterruptedCallback != null) {
				mediaInterruptedCallback.onPlayInterrupted(current, max, null);
			}
		}
	}

	public void loadAudio(String url) {
		setLoading(true);
		try {
			mp = new MediaPlayer();
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				AudioAttributes.Builder builder = new AudioAttributes.Builder();
				builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
				AudioAttributes attributes = builder.build();
				mp.setAudioAttributes(attributes);
			}
			else {
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			}
			if(!isBufferHidden) {
				mp.setOnBufferingUpdateListener(this);
			}
			mp.setOnPreparedListener(this);
			mp.setOnErrorListener(this);
			mp.setOnCompletionListener(this);
			mp.setDataSource(url);
			mp.prepareAsync();
			mp.setLooping(false);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		this.isInitialized = true;
		setLoading(false);
		this.max = mp.getDuration();
		setProgress();
		if(hasInitial) {
			int difference = max - initial;
			if(difference > 1000) {
				mp.seekTo(initial);
			}
			hasInitial = false;
			initial = 0;
		}
		if(resultCallback != null) {
			resultCallback.onResult(true, null);
		}
		play();
	}

	private void setProgress() {
		String progress = hasInitial && initial != 0 ? CodePanUtils.millisToDuration(initial) :
				CodePanUtils.millisToDuration(current);
		this.duration = CodePanUtils.millisToDuration(max);
		String formatted = progress + " / " + duration;
		tvDurationAudio.setText(formatted);
		sbProgressAudio.setMax(max);
		sbProgressAudio.setProgress(hasInitial && initial != 0 ? initial : current);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if(what != -38) {
			setLoading(false);
			if(resultCallback != null) {
				String message = getContext().getString(R.string.loading_audio_failed);
				resultCallback.onResult(false, message);
			}
		}
		return true;
	}

	private void setLoading(boolean isLoading) {
		if(isLoading) {
			btnPlayAudio.setEnabled(false);
			ivPlayAudio.setVisibility(View.GONE);
			ivLoadingAudio.setVisibility(View.VISIBLE);
			ivLoadingAudio.startAnimation(anim);
		}
		else {
			btnPlayAudio.setEnabled(true);
			ivPlayAudio.setVisibility(View.VISIBLE);
			ivLoadingAudio.setVisibility(View.GONE);
			ivLoadingAudio.clearAnimation();
		}
		checkPlayState();
	}

	private void checkPlayState() {
		if(mp != null) {
			ivPlayAudio.setEnabled(!mp.isPlaying());
		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if(mp != null) {
				if(mp.isPlaying()) {
					int current = mp.getCurrentPosition();
					sbProgressAudio.setProgress(current);
					String progress = CodePanUtils.millisToDuration(current);
					String formatted = progress + " / " + duration;
					tvDurationAudio.setText(formatted);
					handler.postDelayed(this, DELAY);
				}
			}
		}
	};

	public void play() {
		if(mp != null && !mp.isPlaying()) {
			mp.start();
			handler.postDelayed(runnable, DELAY);
			checkPlayState();
		}
	}

	public void pause() {
		if(mp != null && mp.isPlaying()) {
			mp.pause();
			handler.removeCallbacks(runnable);
			checkPlayState();
		}
	}

	public int getDuration() {
		return this.max;
	}

	public int getProgress() {
		if(mp != null) {
			return mp.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percentage) {
		float max = (float) mp.getDuration();
		float buffer = ((float) percentage * max) / 100F;
		sbProgressAudio.setSecondaryProgress((int) buffer);
	}

	@Override
	public void onProgressChanged(SeekBar bar, int progress, boolean isTouched) {
		if(isTouched) {
			current = progress;
			mp.seekTo(progress);
			setProgress();
		}
		if(progressCallback != null) {
			progressCallback.onProgress(progress, max);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(completionCallback != null) {
			completionCallback.onCompletion();
		}
		checkPlayState();
	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
	}

	public void setOnCompletionCallback(OnCompletionCallback completionCallback) {
		this.completionCallback = completionCallback;
	}

	public void setOnProgressCallback(OnProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}

	public void setOnMediaInterruptedCallback(OnMediaInterruptedCallback mediaInterruptedCallback) {
		this.mediaInterruptedCallback = mediaInterruptedCallback;
	}

	public void setInitial(int initial, int max) {
		this.hasInitial = true;
		this.initial = initial;
		this.max = max;
	}

	public void setOnResultCallback(OnResultCallback errorCallback) {
		this.resultCallback = errorCallback;
	}

	public void hideBuffer(boolean isBufferHidden) {
		this.isBufferHidden = isBufferHidden;
	}
}
