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
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LensFacing
import androidx.camera.core.CameraState
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
import com.codepan.model.StampData
import com.codepan.utils.CodePanUtils
import com.codepan.utils.Console
import com.codepan.utils.DeviceOrientation
import com.codepan.utils.MotionDetector
import com.codepan.utils.OrientationChangedNotifier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


const val KEY_EVENT_ACTION = "volume_key_action"
const val KEY_EVENT_EXTRA = "volume_key_extra"
const val RATIO_4_3_VALUE = 4.0 / 3.0
const val RATIO_16_9_VALUE = 16.0 / 9.0
const val EYE_BLINK_THRESHOLD = 0.2

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val DEFAULT_RESOLUTION = Size(1080, 1920)

typealias BlinkEyeListener = (didBlink: Boolean) -> Unit
typealias OnCaptureCallback = (fileName: String) -> Unit
typealias OnCameraErrorCallback = (error: CameraError) -> Unit

@ExperimentalGetImage
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

    private var stampList: ArrayList<StampData>? = null
    private var detectMotionBlur: Boolean = false
    private var errorCallback: OnCameraErrorCallback? = null
    private var captureCallback: OnCaptureCallback? = null
    private var orientationNotifier: OrientationChangedNotifier? = null
    private var analyzer: ImageAnalysis.Analyzer? = null
    private var displayId: Int = -1
    private var capture: ImageCapture? = null
    private var analysis: ImageAnalysis? = null
    private var lensFacing: Int = CameraLens.BACK.value
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
        stampList: ArrayList<StampData>? = null,
        detectMotionBlur: Boolean = false,
        analyzer: ImageAnalysis.Analyzer? = null,
        lensFacing: Int = CameraLens.BACK.value,
        orientationNotifier: OrientationChangedNotifier? = null,
        captureCallback: OnCaptureCallback? = null,
        errorCallback: OnCameraErrorCallback? = null,
    ) {
        this.lifecycle = lifecycle
        this.resolution = resolution
        this.folder = folder
        this.stampList = stampList
        this.detectMotionBlur = detectMotionBlur
        this.lensFacing = lensFacing
        this.analyzer = analyzer
        this.orientationNotifier = orientationNotifier
        this.captureCallback = captureCallback
        this.errorCallback = errorCallback
        if (completer.await()) {
            executor = Executors.newSingleThreadExecutor()
            lbm = LocalBroadcastManager.getInstance(context)
            val filter = IntentFilter().apply {
                addAction(KEY_EVENT_ACTION)
            }
            lbm.registerReceiver(receiver, filter)
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
        // switch to the available lens if the selected lens is not present
        when (lensFacing) {
            CameraLens.BACK.value -> {
                if (!hasBackCamera && hasFrontCamera) {
                    lensFacing = CameraLens.FRONT.value
                } else {
                    errorCallback?.invoke(CameraError.NO_CAMERA)
                }
            }

            CameraLens.FRONT.value -> {
                if (!hasFrontCamera && hasBackCamera) {
                    lensFacing = CameraLens.BACK.value
                } else {
                    errorCallback?.invoke(CameraError.NO_CAMERA)
                }
            }
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
        }.build()

        capture = ImageCapture.Builder().also {
            it.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            it.setTargetRotation(rotation)
            it.setTargetResolution(resolution)
        }.build()
        val rational = Rational(resolution.width, resolution.height)
        capture!!.setCropAspectRatio(rational)
        analysis = ImageAnalysis.Builder().also {
            it.setTargetRotation(rotation)
            it.setTargetResolution(resolution)
        }.build().also {
            if (analyzer != null) {
                it.setAnalyzer(executor, analyzer!!)
            }
        }
        provider?.unbindAll()
        if (camera != null) {
            camera!!.cameraInfo.cameraState.removeObservers(this)
        }
        try {
            camera = provider?.bindToLifecycle(
                this, selector, preview, capture, analysis
            )
            preview?.setSurfaceProvider(pvViewFinder.surfaceProvider)
            observeCameraState(camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e("CameraXView", "Use case binding failed", exc)
        }
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
        Console.log("Camera Lens Selection: $lensFacing")
        resetPreview()
    }

    fun takePicture() {
        val stamp = System.currentTimeMillis()
        val dir = context.getDir(folder, Context.MODE_PRIVATE)
        val file = File(dir, "$stamp.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture?.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                if (detectMotionBlur && detector.isMoving) {
                    errorCallback?.invoke(CameraError.MOTION_BLUR)
                    file.delete()
                } else {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    val matrix = Matrix()
                    matrix.postRotate(getImageRotation().toFloat())
                    val exif = ExifInterface(file)
                    exif.rotate(0)
                    val rotated =
                        Bitmap.createBitmap(
                            bitmap, 0, 0,
                            bitmap.width, bitmap.height, matrix, true
                        )
                    val output = if (stampList != null) {
                        val font = context.getString(R.string.calibri_regular)
                        CodePanUtils.stampPhoto(
                            context, rotated, font, 0.035F, stampList
                        )
                    } else {
                        rotated
                    }
                    val fos = FileOutputStream(file)
                    output.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                }
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
                        errorCallback?.invoke(CameraError.CAMERA_BUSY)
                    }

                    else -> {
                        Console.log(cameraState.type)
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
        val ratio = max / min
        if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
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
        orientationNotifier?.onOrientationChanged(orientation)
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

