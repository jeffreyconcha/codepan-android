package com.codepan.utils

import android.graphics.Bitmap
import com.codepan.callback.ValueSetter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class TextDetector(
    private val bitmap: Bitmap,
    private val callback: ValueSetter<String>,
) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processImage() {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image).apply {
            addOnSuccessListener {
                callback.invoke(it.text)
            }
            addOnFailureListener {
                Console.error(it.message)
            }
        }
    }
}