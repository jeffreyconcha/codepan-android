package com.codepan.widget.camerax

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
enum class FlashMode(val value: Int) {
    AUTO(ImageCapture.FLASH_MODE_AUTO),
    ON(ImageCapture.FLASH_MODE_ON),
    OFF(ImageCapture.FLASH_MODE_OFF),
}