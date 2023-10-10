package com.codepan.widget.camerax

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.codepan.utils.Console
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@ExperimentalGetImage
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private class BlinkAnalyzer(val listener: BlinkEyeListener) : ImageAnalysis.Analyzer {
    private var _isEyeClose: Boolean = false

    override fun analyze(proxy: ImageProxy) {
        val media = proxy.image
        if (media != null) {
            val rotation = proxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(media, rotation)
            val option = FaceDetectorOptions.Builder().also {
                it.setContourMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                it.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                it.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                it.setMinFaceSize(0.15f)
                it.enableTracking()
            }.build()
            val detector = FaceDetection.getClient(option)
            detector.process(image).also {
                it.addOnSuccessListener { faces ->
                    for (face in faces) {
                        val left = face.leftEyeOpenProbability ?: 1f
                        val right = face.rightEyeOpenProbability ?: 1f
                        val isEyeClose =
                            left <= EYE_BLINK_THRESHOLD || right <= EYE_BLINK_THRESHOLD
                        if (_isEyeClose && !isEyeClose) {
                            listener.invoke(true)
                        }
                        _isEyeClose = isEyeClose
                    }
                    proxy.close()
                }
                it.addOnFailureListener { ex ->
                    Console.log(ex.message)
                    proxy.close()
                }
            }
        }
    }
}