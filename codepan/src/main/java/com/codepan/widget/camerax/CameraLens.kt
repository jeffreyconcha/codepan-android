package com.codepan.widget.camerax

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
enum class CameraLens(val value: Int) {
    FRONT(CameraSelector.LENS_FACING_FRONT),
    BACK(CameraSelector.LENS_FACING_BACK),
}