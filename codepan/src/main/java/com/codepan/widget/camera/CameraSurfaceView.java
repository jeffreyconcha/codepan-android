package com.codepan.widget.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.codepan.R;
import com.codepan.callback.Interface.OnCameraChangeCallback;
import com.codepan.model.StampData;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.Console;
import com.codepan.utils.DeviceOrientation;
import com.codepan.utils.MotionDetector;
import com.codepan.utils.OrientationChangedNotifier;
import com.codepan.widget.FocusIndicatorView;
import com.codepan.widget.camera.Callback.OnCameraErrorCallback;
import com.codepan.widget.camera.Callback.OnCaptureCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CameraSurfaceView extends SurfaceView implements
	SurfaceHolder.Callback, PictureCallback {

	public enum CameraError {
		UNABLE_TO_LOAD,
		MOTION_BLUR,
	}

	private final int CAMERA_ID = 1;
	private final int OPTIMAL_RESO = 307200;//640 x 480
	private final int HIGH_RESO = 2073600;//1920 x 1080
	private final int IMAGE_ROTATION_FRONT = 270;
	private final int IMAGE_ROTATION_BACK = 90;

	private boolean isFrontCamInverted, hasAutoFocus, isCaptured, hasFlash,
		hasFrontCam, hasStopped, isScaled;
	private int cameraSelection, maxWidth, maxHeight, picWidth, picHeight, maxZoom;
	private boolean isLandscape, detectMotionBlur, withShutterSound;
	private OnCameraChangeCallback cameraChangeCallback;
	private OnCameraErrorCallback cameraErrorCallback;
	private FocusIndicatorView focusIndicatorView;
	private OnCaptureCallback captureCallback;
	private ArrayList<StampData> stampList;
	private SurfaceHolder surfaceHolder;
	private MotionDetector detector;
	private String folder, prefix;
	private Parameters params;
	private String flashMode;
	private Context context;
	private Camera camera;

	@SuppressWarnings("deprecation")
	public CameraSurfaceView(
		Context context,
		OnCameraErrorCallback cameraErrorCallback,
		OrientationChangedNotifier notifier,
		int cameraSelection,
		String flashMode,
		String folder,
		int maxWidth,
		int maxHeight,
		boolean detectMotionBlur,
		boolean withShutterSound
	) {
		super(context);
		PackageManager pm = context.getPackageManager();
		this.hasFrontCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
		this.hasAutoFocus = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		this.camera = getAvailableCamera(cameraSelection);
		this.cameraErrorCallback = cameraErrorCallback;
		this.detector = new MotionDetector(context, notifier);
		this.detectMotionBlur = detectMotionBlur;
		this.withShutterSound = withShutterSound;
		if(camera != null) {
			this.cameraSelection = cameraSelection;
			this.flashMode = flashMode;
			this.maxHeight = maxHeight;
			this.maxWidth = maxWidth;
			this.context = context;
			this.folder = folder;
			this.params = camera.getParameters();
			this.surfaceHolder = getHolder();
			this.surfaceHolder.addCallback(this);
			this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			final int orientation = context.getResources().getConfiguration().orientation;
			this.isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
			List<String> flashModeList = params.getSupportedFlashModes();
			if(flashModeList != null && !flashModeList.isEmpty()) {
				for(String mode : flashModeList) {
					if(mode.equals(Parameters.FLASH_MODE_ON)) {
						this.hasFlash = true;
						break;
					}
				}
			}
		}
		else {
			if(cameraErrorCallback != null) {
				cameraErrorCallback.onCameraError(CameraError.UNABLE_TO_LOAD);
			}
		}
		if(params.isZoomSupported()) {
			maxZoom = params.getMaxZoom();
		}
		else {
			Console.log("Zoom is not supported for the selected camera.");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		if(hasAutoFocus) {
			camera.getParameters().setFocusMode(Parameters.FOCUS_MODE_AUTO);
			camera.autoFocus(null);
		}
	}

	public int getMaxZoom() {
		return this.maxZoom;
	}

	@SuppressLint("ObsoleteSdkInt")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(holder);
			params = camera.getParameters();
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				int orientation = 90;
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(CAMERA_ID, info);
				if(info.canDisableShutterSound) {
					camera.enableShutterSound(withShutterSound);
				}
				if(hasFrontCam && cameraSelection == CameraInfo.CAMERA_FACING_FRONT) {
					orientation = (info.orientation) % 360;
					orientation = (360 - orientation) % 360;
					isFrontCamInverted = orientation == 270;
				}
				orientation = isLandscape ? 0 : orientation;
				camera.setDisplayOrientation(orientation);
			}
			else {
				params.set("orientation", "portrait");
			}
			if(hasFlash && cameraSelection == CameraInfo.CAMERA_FACING_BACK) {
				params.setFlashMode(flashMode);
			}
			List<Size> previewSizes = params.getSupportedPreviewSizes();
			List<Size> pictureSizes = params.getSupportedPictureSizes();
			int previewWidth = 0;
			int previewHeight = 0;
			int pictureWidth = 0;
			int pictureHeight = 0;
			float maxOutputHeight = 0f;
			for(Size s : previewSizes) {
				float ratio = (float) s.width / (float) s.height;
				float outputHeight = isLandscape ?
					(float) maxWidth / ratio :
					(float) maxWidth * ratio;
				if(maxOutputHeight < outputHeight) {
					maxOutputHeight = outputHeight;
					previewHeight = s.height;
					previewWidth = s.width;
				}
			}
			List<Size> sizes = new ArrayList<>();
			for(Size s : pictureSizes) {
				Console.debug("AVAILABLE: " + s.width + "x" + s.height);
				int resolution = s.width * s.height;
				if(resolution <= HIGH_RESO && resolution >= OPTIMAL_RESO) {
					sizes.add(s);
				}
			}
			if(sizes.isEmpty()) {
				sizes = pictureSizes;
			}
			int count = sizes.size();
			Size first = sizes.get(0);
			Size last = sizes.get(count - 1);
			if(first.width * first.height > last.width * last.height) {
				pictureWidth = first.width;
				pictureHeight = first.height;
			}
			else {
				pictureWidth = last.width;
				pictureHeight = last.height;
			}
			Console.log("SELECTED: " + previewWidth + "x" + previewHeight);
			params.setRotation(0);
			params.setPreviewSize(previewWidth, previewHeight);
			params.setPictureSize(pictureWidth, pictureHeight);
			camera.setParameters(params);
			camera.startPreview();
		}
		catch(Exception e) {
			e.printStackTrace();
			camera.release();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		if(camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
		surfaceHolder.removeCallback(this);
		if(cameraChangeCallback != null) {
			cameraChangeCallback.onCameraChange(false);
		}
	}

	public Camera getAvailableCamera(int selection) {
		final int DEFAULT = 0;
		Camera camera = null;
		try {
			int count = Camera.getNumberOfCameras();
			if(hasFrontCam) {
				if(count >= 2) {
					camera = Camera.open(selection);
				}
				else {
					camera = Camera.open(DEFAULT);
				}
			}
			else {
				camera = Camera.open(DEFAULT);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return camera;
	}

	public DeviceOrientation getOrientation() {
		return detector.getOrientation();
	}

	public void takePicture() {
		if(camera != null) {
			isCaptured = true;
			camera.takePicture(null, null, this);
		}
	}

	public void reset() {
		if(camera != null) {
			camera.startPreview();
			isCaptured = false;
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		if(detectMotionBlur && detector.isMoving()) {
			if(cameraErrorCallback != null) {
				cameraErrorCallback.onCameraError(CameraError.MOTION_BLUR);
			}
		}
		else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16 * 1024];
			Parameters params = camera.getParameters();
			Size size = params.getPictureSize();
			float memory = (float) (size.height * size.width) / 1024000F;
			if(memory > 4F) {
				options.inSampleSize = 4;
			}
			else if(memory > 3F) {
				options.inSampleSize = 2;
			}
			Bitmap src = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			String fileName = System.currentTimeMillis() + ".jpg";
			if(prefix != null) {
				fileName = prefix + "-" + fileName;
			}
			if(isScaled) {
				src = scale(src);
			}
			Matrix matrix = new Matrix();
			matrix.postRotate(getImageRotation());
			Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), matrix, true);
			if(stampList != null) {
				String font = context.getString(R.string.calibri_regular);
				bitmap = CodePanUtils.stampPhoto(context, bitmap, font, 0.035F, stampList);
			}
			saveBitmap(fileName, bitmap);
		}
	}

	public void saveBitmap(final String fileName, final Bitmap bitmap) {
		final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
			File dir = context.getDir(folder, Context.MODE_PRIVATE);
			File file = new File(dir, fileName);
			if(file.exists() && file.length() > 0 && CodePanUtils.isValidImage(file)) {
				captureCallback.onCapture(fileName);
			}
			else {
				captureCallback.onCapture(null);
			}
			return true;
		});
		Thread bg = new Thread(() -> {
			Looper.prepare();
			try {
				try {
					File dir = context.getDir(folder, Context.MODE_PRIVATE);
					File file = new File(dir, fileName);
					if(!dir.exists()) {
						dir.mkdir();
					}
					FileOutputStream fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.close();
					handler.obtainMessage().sendToTarget();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		});
		bg.start();
	}

	public void setOnCaptureCallback(OnCaptureCallback captureCallback) {
		this.captureCallback = captureCallback;
	}

	private Bitmap scale(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		if(srcWidth > picWidth) {
			width = picWidth;
			float ratio = (float) picWidth / (float) srcWidth;
			height = (int) ((float) bitmap.getHeight() * ratio);
		}
		else {
			if(srcHeight > picHeight) {
				height = picHeight;
				float ratio = (float) picHeight / (float) srcHeight;
				width = (int) ((float) bitmap.getWidth() * ratio);
			}
		}
		return Bitmap.createScaledBitmap(bitmap, width, height, false);
	}

	public void setStampList(ArrayList<StampData> stampList) {
		this.stampList = stampList;
	}

	public void setMaxPictureSize(int width, int height) {
		this.isScaled = true;
		this.picWidth = width;
		this.picHeight = height;
	}

	public boolean isCaptured() {
		return isCaptured;
	}

	public Camera getCamera() {
		return this.camera;
	}

	public boolean canSwitchCamera() {
		int count = Camera.getNumberOfCameras();
		return hasFrontCam && count >= 2;
	}

	public boolean hasFlash() {
		return this.hasFlash;
	}

	public boolean hasAutoFocus() {
		return this.hasAutoFocus;
	}

	public boolean hasStopped() {
		return this.hasStopped;
	}

	public void stopCamera() {
		if(camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
		if(surfaceHolder != null) {
			surfaceHolder.removeCallback(this);
		}
		this.hasStopped = true;
	}

	/**
	 * @param container must be nested in LinearLayout to enable
	 *                  centerCrop scale
	 */
	public void fullScreenToContainer(ViewGroup container) {
		if(camera != null) {
			Parameters params = camera.getParameters();
			float maxOutputHeight = 0f;
			float maxRatio = 0f;
			List<Size> sizes = params.getSupportedPreviewSizes();
			for(Size s : sizes) {
				float ratio = (float) s.width / (float) s.height;
				float outputHeight = isLandscape ?
					(float) maxWidth / ratio :
					(float) maxWidth * ratio;
				if(maxOutputHeight < outputHeight) {
					maxOutputHeight = outputHeight;
					maxRatio = ratio;
				}
			}
			LayoutParams lp = (LayoutParams) container.getLayoutParams();
			if(maxOutputHeight >= maxHeight) {
				lp.width = maxWidth;
				lp.height = (int) maxOutputHeight;
			}
			else {
				float maxOutputWidth = (float) maxHeight / maxRatio;
				lp.height = maxHeight;
				lp.width = (int) maxOutputWidth;
			}
		}
	}

	public int getImageRotation() {
		DeviceOrientation orientation = detector.getOrientation();
		int adjustment = (orientation.getDegrees() - 90);
		int count = Camera.getNumberOfCameras();
		if(count >= 2) {
			switch(cameraSelection) {
				case CameraInfo.CAMERA_FACING_FRONT:
					if(isFrontCamInverted) {
						return IMAGE_ROTATION_BACK + adjustment;
					}
					else {
						return IMAGE_ROTATION_FRONT + adjustment;
					}
				case CameraInfo.CAMERA_FACING_BACK:
					return IMAGE_ROTATION_BACK - adjustment;
			}
		}
		else {
			if(hasFrontCam) {
				return IMAGE_ROTATION_FRONT + adjustment;
			}
			else {
				return IMAGE_ROTATION_BACK - adjustment;
			}
		}
		return 0;
	}

	@SuppressLint("NewApi")
	public void tapToFocus(final Rect tfocusRect) {
		if(camera != null) {
			try {
				List<Area> focusList = new ArrayList<>();
				Area focusArea = new Area(tfocusRect, 1000);
				focusList.add(focusArea);
				Parameters params = camera.getParameters();
				params.setFocusAreas(focusList);
				params.setMeteringAreas(focusList);
				camera.setParameters(params);
				camera.cancelAutoFocus();
				camera.autoFocus((success, camera) -> {
					if(success) {
						String focusMode = Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
						Parameters params1 = camera.getParameters();
						List<String> focusModeList = params1.getSupportedFocusModes();
						if(focusModeList.contains(focusMode)) {
							if(!params1.getFocusMode().equals(focusMode)) {
								params1.setFocusMode(focusMode);
								if(params1.getMaxNumFocusAreas() > 0) {
									params1.setFocusAreas(null);
								}
								camera.setParameters(params1);
								camera.startPreview();
							}
						}
					}
				});
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(hasAutoFocus && cameraSelection == CameraInfo.CAMERA_FACING_BACK) {
			final int SIZE = 60;
			final int DELAY = 1000;
			int width = this.getWidth();
			int height = this.getHeight();
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				float x = event.getX();
				float y = event.getY();
				Rect touchRect = new Rect(
					(int) (x - SIZE),
					(int) (y - SIZE),
					(int) (x + SIZE),
					(int) (y + SIZE));
				int left = touchRect.left * 2000 / width - 1000;
				int top = touchRect.top * 2000 / height - 1000;
				int right = touchRect.right * 2000 / width - 1000;
				int bottom = touchRect.bottom * 2000 / height - 1000;
				final Rect targetFocusRect = new Rect(left, top, right, bottom);
				tapToFocus(targetFocusRect);
				if(focusIndicatorView != null) {
					focusIndicatorView.setHaveTouch(true, touchRect);
					focusIndicatorView.invalidate();
					Handler handler = new Handler();
					handler.postDelayed(() -> {
						focusIndicatorView.setHaveTouch(false, new Rect(0, 0, 0, 0));
						focusIndicatorView.invalidate();
					}, DELAY);
				}
			}
		}
		else {
			super.onTouchEvent(event);
		}
		return false;
	}

	public void setFocusIndicatorView(FocusIndicatorView focusIndicatorView) {
		this.focusIndicatorView = focusIndicatorView;
	}

	public void setOnCameraChangeCallback(OnCameraChangeCallback cameraChangeCallback) {
		this.cameraChangeCallback = cameraChangeCallback;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		detector.dispose();
	}

	public void updateZoom(int zoom) {
		if(params.isZoomSupported()) {
			if(zoom <= params.getMaxZoom()) {
				params.setZoom(zoom);
				camera.setParameters(params);
			}
		}
	}
}