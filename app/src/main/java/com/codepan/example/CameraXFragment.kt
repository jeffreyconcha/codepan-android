package com.codepan.example

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.coroutineScope
import com.codepan.app.CPFragment
import com.codepan.model.StampData
import com.codepan.permission.PermissionEvents
import com.codepan.permission.PermissionHandler
import com.codepan.permission.PermissionType
import com.codepan.utils.CodePanUtils
import com.codepan.utils.Console
import com.codepan.widget.camerax.BlinkAnalyzer
import com.codepan.widget.camerax.BlinkEyeListener
import com.codepan.widget.camerax.CameraError
import com.codepan.widget.camerax.CameraLens
import com.codepan.widget.camerax.CameraXNotifiers
import com.codepan.widget.camerax.CameraXView
import com.codepan.widget.camerax.FlashMode
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalGetImage
class CameraXFragment : CPFragment(), PermissionEvents {

    private lateinit var cxvCamera: CameraXView
    private lateinit var btnSwitchCamera: Button;
    private lateinit var btnCaptureCamera: Button;
    private lateinit var btnFlashOnCamera: Button
    private lateinit var btnFlashOffCamera: Button


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.camerax_sample_layout, container, false)
        btnSwitchCamera = view.findViewById(R.id.btnSwitchCamera)
        btnCaptureCamera = view.findViewById(R.id.btnCaptureCamera)
        btnFlashOnCamera = view.findViewById(R.id.btnFlashOnCamera)
        btnFlashOffCamera = view.findViewById(R.id.btnFlashOffCamera)
        cxvCamera = view.findViewById(R.id.cxvCamera);
        handler.checkPermissions()
        btnSwitchCamera.setOnClickListener {
            cxvCamera.switchCamera()
        }
        btnCaptureCamera.setOnClickListener {
            cxvCamera.takePicture()
        }
        btnFlashOnCamera.setOnClickListener {
            cxvCamera.setFlashMode(FlashMode.ON)
        }
        btnFlashOffCamera.setOnClickListener {
            cxvCamera.setFlashMode(FlashMode.OFF)
        }
        return view;
    }

    override val handler: PermissionHandler
        get() = PermissionHandler(
            activity,
            this,
            PermissionType.CAMERA,
        )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionsResult(
        handler: PermissionHandler,
        isGranted: Boolean,
    ) {
        Console.log("onPermissionsResult: $isGranted")
        if (isGranted) {
            val stampList = arrayListOf<StampData>()
            val address = "San Jose St., Brgy. Burangaw, Pasig City, Metro Manila PH";
            val multiline = CodePanUtils.toMultiline(address, 25)
            stampList.add(StampData("Jeffrey Concha", Paint.Align.LEFT))
            stampList.add(StampData("Software Engineer", Paint.Align.LEFT))
            stampList.add(StampData("October 9, 2023", Paint.Align.RIGHT))
            if (multiline.contains("\n")) {
                for (line in multiline.split("\n")) {
                    val stamp = StampData()
                    stamp.data = line
                    stamp.alignment = Paint.Align.RIGHT
                    stampList.add(stamp)
                }
            } else {
                val stamp = StampData()
                stamp.data = address
                stamp.alignment = Paint.Align.RIGHT
                stampList.add(stamp)
            }
            lifecycle.coroutineScope.launch {
                cxvCamera.initialize(
                    lifecycle,
                    folder = "camerax",
                    stampList = stampList,
                    detectMotionBlur = true,
                    lensFacing = CameraLens.FRONT,
                    analyzer = BlinkAnalyzer(object : BlinkEyeListener {
                        override fun onBlink() {
                            Console.log("You blinked!!!")
                        }

                        override fun onFaceChange(hasFace: Boolean) {
                            Console.log("Has face: $hasFace")
                        }
                    }),
                    notifiers = object : CameraXNotifiers {
                        override fun onCapture(file: File) {
                            Console.log(file.name)
                        }

                        override fun onError(error: CameraError) {
                            showError(error)
                        }

                        override fun onLoadCamera(camera: CameraXView) {
                            if (camera.hasFlash) {
                                btnFlashOnCamera.visibility = View.VISIBLE
                                btnFlashOffCamera.visibility = View.VISIBLE
                            } else {
                                btnFlashOnCamera.visibility = View.GONE
                                btnFlashOffCamera.visibility = View.GONE
                            }
                        }
                    },
                )
            }
        } else {
            handler.checkPermissions()
        }
    }

    fun showError(error: CameraError) {
        Console.log(error)
    }

    override fun onShowPermissionRationale(
        handler: PermissionHandler,
        permission: PermissionType,
    ) {
        Log.i("show permission", "true")
    }
}