package com.codepan.widget.camerax

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.window.WindowManager
import com.codepan.R
import com.codepan.utils.Console
import com.codepan.utils.DeviceOrientation
import com.codepan.utils.MotionDetector
import com.codepan.utils.OrientationChangedNotifier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


const val KEY_EVENT_ACTION = "volume_key_action"
const val KEY_EVENT_EXTRA = "volume_key_extra"
const val RATIO_4_3_VALUE = 4.0 / 3.0
const val RATIO_16_9_VALUE = 16.0 / 9.0

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val DEFAULT_RESOLUTION = Size(1080, 1920)

typealias LumaListener = (luma: Double) -> Unit

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraXView(
    ctx: Context,
    attrs: AttributeSet,
) : FrameLayout(ctx, attrs), LifecycleOwner, OrientationChangedNotifier {

    private val inflater = LayoutInflater.from(context)
    private lateinit var pvViewFinder: PreviewView
    private lateinit var executor: ExecutorService
    private lateinit var lbm: LocalBroadcastManager
    private lateinit var wm: WindowManager
    private lateinit var lifecycle: Lifecycle
    private lateinit var folder: String
    private lateinit var resolution: Size

    private var notifier: OrientationChangedNotifier? = null
    private var displayId: Int = -1
    private var capture: ImageCapture? = null
    private var analyzer: ImageAnalysis? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var provider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var camera: Camera? = null
    private val completer = CompletableDeferred<Boolean>()
    private val detector = MotionDetector(context, this)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    //capture
                }
            }
        }
    }

    private val hasFrontCamera: Boolean
        get() = provider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false

    private val hasBackCamera: Boolean
        get() = provider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    private val scope: LifecycleCoroutineScope
        get() = lifecycle.coroutineScope

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val view = inflater.inflate(R.layout.camerax_layout, this, false)
        pvViewFinder = view.findViewById(R.id.pvViewFinder)
        removeAllViews()
        addView(view)
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        completer.complete(true)
    }

    suspend fun initialize(
        lifecycle: Lifecycle,
        folder: String,
        resolution: Size = DEFAULT_RESOLUTION,
        notifier: OrientationChangedNotifier? = null,
    ) {
        this.lifecycle = lifecycle
        this.resolution = resolution
        this.folder = folder
        this.notifier = notifier
        if (completer.await()) {
            executor = Executors.newSingleThreadExecutor()
            lbm = LocalBroadcastManager.getInstance(context)
            val filter = IntentFilter().apply {
                addAction(KEY_EVENT_ACTION)
            }
            lbm.registerReceiver(receiver, filter);
            wm = WindowManager(context)
            pvViewFinder.post {
                displayId = pvViewFinder.display.displayId
                scope.launch {
                    setUpCamera()
                }
            }
        }
    }

    private suspend fun setUpCamera() {
        provider = ProcessCameraProvider.getInstance(context).await()
        lensFacing = when {
            hasBackCamera -> CameraSelector.LENS_FACING_BACK
            hasFrontCamera -> CameraSelector.LENS_FACING_FRONT
            else -> throw IllegalStateException("Back and front camera are unavailable")
        }
        resetPreview()
    }

    fun resetPreview() {
        val metrics = wm.getCurrentWindowMetrics().bounds
        val ratio = aspectRatio(metrics.width(), metrics.height())
        val rotation = pvViewFinder.display.rotation
        val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        preview = Preview.Builder().also {
            it.setTargetAspectRatio(ratio)
            it.setTargetRotation(rotation)
//            it.setTargetResolution(resolution)
        }.build()

        capture = ImageCapture.Builder().also {
            it.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//            it.setTargetAspectRatio(ratio)
            it.setTargetRotation(rotation)
            it.setTargetResolution(resolution)
        }.build()
        val rational = Rational(resolution.width, resolution.height)
        capture!!.setCropAspectRatio(rational)
        analyzer = ImageAnalysis.Builder().also {
//            it.setTargetAspectRatio(ratio)
            it.setTargetRotation(rotation)
            it.setTargetResolution(resolution)
        }.build().also {
            it.setAnalyzer(executor, LuminosityAnalyzer { value ->
//                Log.d("LUMINOSITY", "Average luminosity: $value")
            })
        }
        provider?.unbindAll()
        if (camera != null) {
            camera!!.cameraInfo.cameraState.removeObservers(this)
        }
        try {
            camera = provider?.bindToLifecycle(
                this, selector, preview, capture, analyzer
            )
            preview?.setSurfaceProvider(pvViewFinder.surfaceProvider)
            observeCameraState(camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e("CameraXView", "Use case binding failed", exc)
        }
        Console.log("SETUP CAMERA SUCCESS");
    }

    fun switchCamera() {
        lensFacing = when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> {
                if (hasBackCamera) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    lensFacing
                }
            }

            CameraSelector.LENS_FACING_BACK -> {
                if (hasFrontCamera) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    lensFacing
                }
            }

            else ->
                lensFacing
        }
        Console.log("Lens Selection: $lensFacing");
        resetPreview()
    }

    fun takePicture() {
        val stamp = System.currentTimeMillis()
        val dir = context.getDir(folder, Context.MODE_PRIVATE)
        val file = File(dir, "$stamp.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture?.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                val matrix = Matrix()
                matrix.postRotate(getImageRotation().toFloat())
                val exif = ExifInterface(file)
                exif.rotate(0)
                val rotated =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                val fos = FileOutputStream(file)
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Console.error(exception)
            }
        })

    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this) { cameraState ->
            run {
                when (cameraState.type) {
                    CameraState.Type.PENDING_OPEN -> {
                        Log.i("CAMERA STATE", "PENDING OPEN");
                    }

                    CameraState.Type.OPENING -> {
                        Log.i("CAMERA STATE", "OPENING");
                    }

                    CameraState.Type.OPEN -> {
                        Log.i("CAMERA STATE", "OPEN");
                    }

                    CameraState.Type.CLOSING -> {
                        Log.i("CAMERA STATE", "CLOSING");
                    }

                    CameraState.Type.CLOSED -> {
                        Log.i("CAMERA STATE", "CLOSED");
                    }
                }
            }
            cameraState.error?.let { error ->
                Console.log(error)
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val max = max(width, height).toDouble()
        val min = min(width, height).toDouble()
        val ratio = max / min;
        if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            if (listeners.isEmpty()) {
                image.close()
                return
            }
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0
            lastAnalyzedTimestamp = frameTimestamps.first
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            listeners.forEach { it(luma) }
            image.close()
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        executor.shutdown()
        lbm.unregisterReceiver(receiver)
    }

    override fun onOrientationChanged(orientation: DeviceOrientation) {
//        Console.log(orientation.degrees)
        notifier?.onOrientationChanged(orientation)
        when (lensFacing) {
            CameraSelector.LENS_FACING_BACK -> {
                val target = when (orientation) {
                    DeviceOrientation.PORTRAIT_90 -> Surface.ROTATION_90
                    DeviceOrientation.LANDSCAPE_180 -> Surface.ROTATION_270
                    DeviceOrientation.LANDSCAPE_0 -> Surface.ROTATION_0
                    DeviceOrientation.PORTRAIT_270 -> Surface.ROTATION_270
                }

            }

            CameraSelector.LENS_FACING_FRONT -> {

            }
        }
    }

    fun getImageRotation(): Int {
        val orientation = detector.orientation
        return when (lensFacing) {
            CameraSelector.LENS_FACING_BACK -> {
                return if (orientation.isPortrait) {
                    orientation.degrees
                } else {
                    orientation.opposite
                }
            }

            else -> {
                orientation.opposite
            }
        }
    }
}

