package com.codepan.example

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.lifecycle.coroutineScope
import com.codepan.app.CPFragment
import com.codepan.permission.PermissionEvents
import com.codepan.permission.PermissionHandler
import com.codepan.permission.PermissionType
import com.codepan.utils.Console
import com.codepan.widget.camerax.CameraXView
import kotlinx.coroutines.launch

class CameraXFragment : CPFragment(), PermissionEvents {

    private lateinit var cxvCamera: CameraXView
    private lateinit var btnSwitchCamera: Button;
    private lateinit var btnCaptureCamera: Button;


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.camerax_sample_layout, container, false)
        btnSwitchCamera = view.findViewById(R.id.btnSwitchCamera)
        btnCaptureCamera = view.findViewById(R.id.btnCaptureCamera)
        cxvCamera = view.findViewById(R.id.cxvCamera);
        handler.checkPermissions()
        btnSwitchCamera.setOnClickListener {
            cxvCamera.switchCamera()
        }
        btnCaptureCamera.setOnClickListener {
            cxvCamera.takePicture()
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
            lifecycle.coroutineScope.launch {
                cxvCamera.initialize(lifecycle, "camerax")
            }
        } else {
            handler.checkPermissions()
        }
    }

    override fun onShowPermissionRationale(
        handler: PermissionHandler,
        permission: PermissionType,
    ) {
        Log.i("show permission", "true")
    }
}