package com.codepan.example;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.codepan.model.StampData;
import com.codepan.time.DateTime;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.FocusIndicatorView;
import com.codepan.widget.camera.Callback.OnCameraErrorCallback;
import com.codepan.widget.camera.Callback.OnCaptureCallback;
import com.codepan.widget.camera.CameraSurfaceView;
import com.codepan.widget.camera.CameraSurfaceView.CameraError;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CameraFragment extends Fragment implements OnClickListener, OnCaptureCallback,
    OnCameraErrorCallback {

    private final String flashMode = Camera.Parameters.FLASH_MODE_OFF;
    private final long TRANS_DELAY = 300;
    private final long FADE_DELAY = 750;
    private final String FOLDER = "camera";

    private CodePanButton btnCaptureCamera, btnSwitchCamera, btnBackCamera;
    private int cameraSelection, maxWidth, maxHeight;
    private CameraSurfaceView surfaceView;
    private FocusIndicatorView dvCamera;
    private FragmentManager manager;
    private FrameLayout flCamera;
    private ImageView ivCamera;
    private MainActivity main;
    private View vCamera;

    @Override
    public void onResume() {
        super.onResume();
        if (surfaceView != null && surfaceView.getCamera() == null) {
            resetCamera(0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        manager = main.getSupportFragmentManager();
        maxWidth = CodePanUtils.getMaxWidth(main);
        maxHeight = CodePanUtils.getMaxHeight(main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_sample_layout, container, false);
        dvCamera = view.findViewById(R.id.dvCamera);
        flCamera = view.findViewById(R.id.flCamera);
        btnCaptureCamera = view.findViewById(R.id.btnCaptureCamera);
        btnSwitchCamera = view.findViewById(R.id.btnSwitchCamera);
        btnBackCamera = view.findViewById(R.id.btnBackCamera);
        ivCamera = view.findViewById(R.id.ivCamera);
        vCamera = view.findViewById(R.id.vCamera);
        btnCaptureCamera.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);
        btnBackCamera.setOnClickListener(this);
        resetCamera(TRANS_DELAY);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCaptureCamera:
                if (surfaceView != null && !surfaceView.isCaptured()) {
                    surfaceView.takePicture();
                }
                break;
            case R.id.btnSwitchCamera:
                if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT) {
                    cameraSelection = CameraInfo.CAMERA_FACING_BACK;
                }
                else {
                    cameraSelection = CameraInfo.CAMERA_FACING_FRONT;
                }
                resetCamera(0);
                break;
            case R.id.btnBackCamera:
                manager.popBackStack();
                break;
        }
    }

    @Override
    public void onCapture(String fileName) {
        ivCamera.setVisibility(View.VISIBLE);
        Bitmap bitmap = CodePanUtils.getBitmapImage(main, FOLDER, fileName);
        ivCamera.setImageBitmap(bitmap);
    }

    public void resetCamera(long delay) {
        if (vCamera != null) vCamera.setVisibility(View.VISIBLE);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (surfaceView != null) surfaceView.stopCamera();
            surfaceView = new CameraSurfaceView(main, this,
                cameraSelection, flashMode, FOLDER, maxWidth, maxHeight, true);
            surfaceView.setOnCaptureCallback(this);
            surfaceView.setFocusIndicatorView(dvCamera);
            surfaceView.fullScreenToContainer(flCamera);
            DateTime now = DateTime.Companion.now();
            String date = now.getReadableDate(true, true, false);
            String time = now.getReadableTime(true);
            ArrayList<StampData> stampList = new ArrayList<>();
            StampData stamp = new StampData();
            stamp.data = date + " " + time;
            stamp.alignment = Paint.Align.LEFT;
            surfaceView.setStampList(stampList);
            surfaceView.setMaxPictureSize(640, 360);
            if (flCamera.getChildCount() > 1) {
                flCamera.removeViewAt(0);
            }
            flCamera.addView(surfaceView, 0);
            CodePanUtils.fadeOut(vCamera, FADE_DELAY);
            if (!surfaceView.canSwitchCamera()) {
                btnSwitchCamera.setVisibility(View.GONE);
            }
        }, delay);
    }

    @Override
    public void onCameraError(CameraError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Camera Failed");
        if(error == CameraError.UNABLE_TO_LOAD) {
            builder.setMessage("Failed to load camera please try to restart your device.");
        }
        else {
            builder.setMessage("Motion blur detected. Please do not move your camera while capturing photo.");
        }
        builder.setPositiveButton("OK", (dialog, id) -> {
            dialog.dismiss();
            manager.popBackStack();
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
