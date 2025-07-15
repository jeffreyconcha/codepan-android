package com.codepan.widget.camerax

import android.annotation.SuppressLint
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
import androidx.window.layout.WindowMetricsCalculator
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

interface CameraXNotifiers {
    fun onCapture(file: File)
    fun onError(error: CameraError)
    fun onLoadCamera(camera: CameraXView)
}

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
    private lateinit var lc: Lifecycle
    private lateinit var folder: String
    private lateinit var resolution: Size

    private var stampList: ArrayList<StampData>? = null
    private var detectMotionBlur: Boolean = false
    private var notifiers: CameraXNotifiers? = null
    private var orientationNotifier: OrientationChangedNotifier? = null
    private var analyzer: ImageAnalysis.Analyzer? = null
    private var displayId: Int = -1
    private var capture: ImageCapture? = null
    private var analysis: ImageAnalysis? = null
    private var _lensFacing: CameraLens = CameraLens.BACK
    private var _flashMode: FlashMode = FlashMode.OFF
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

    val orientation: DeviceOrientation
        get() = detector.orientation

    val hasFrontCamera: Boolean
        get() = provider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false

    val hasBackCamera: Boolean
        get() = provider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    val hasFlash: Boolean
        get() = camera!!.cameraInfo.hasFlashUnit()

    val lensFacing: CameraLens
        get() = _lensFacing

    val flashMode: FlashMode
        get() = _flashMode

    private val scope: LifecycleCoroutineScope
        get() = lc.coroutineScope

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
        lensFacing: CameraLens = CameraLens.BACK,
        flashMode: FlashMode = FlashMode.OFF,
        orientationNotifier: OrientationChangedNotifier? = null,
        notifiers: CameraXNotifiers? = null,
    ) {
        this.lc = lifecycle
        this.resolution = resolution
        this.folder = folder
        this.stampList = stampList
        this.detectMotionBlur = detectMotionBlur
        this._lensFacing = lensFacing
        this._flashMode = flashMode
        this.analyzer = analyzer
        this.orientationNotifier = orientationNotifier
        this.notifiers = notifiers
        if (completer.await()) {
            executor = Executors.newSingleThreadExecutor()
            lbm = LocalBroadcastManager.getInstance(context)
            val filter = IntentFilter().apply {
                addAction(KEY_EVENT_ACTION)
            }
            lbm.registerReceiver(receiver, filter)
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
        when (_lensFacing) {
            CameraLens.BACK -> {
                if (!hasBackCamera) {
                    if (hasFrontCamera) {
                        _lensFacing = CameraLens.FRONT
                    } else {
                        notifiers?.onError(CameraError.NO_CAMERA)
                    }
                }
            }

            CameraLens.FRONT -> {
                if (!hasFrontCamera) {
                    if (hasBackCamera) {
                        _lensFacing = CameraLens.BACK
                    } else {
                        notifiers?.onError(CameraError.NO_CAMERA)
                    }
                }
            }
        }
        resetPreview()
    }

    @SuppressLint("RestrictedApi")
    fun resetPreview() {
        val calculator = WindowMetricsCalculator.getOrCreate()
        val metrics = calculator.computeCurrentWindowMetrics(context).bounds
        val ratio = aspectRatio(metrics.width(), metrics.height())
        val rotation = pvViewFinder.display.rotation
        val selector = CameraSelector.Builder().requireLensFacing(_lensFacing.value).build()
        preview = Preview.Builder().also {
            it.setTargetAspectRatio(ratio)
            it.setTargetRotation(rotation)
        }.build()

        capture = ImageCapture.Builder().also {
            it.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            it.setTargetRotation(rotation)
            it.setTargetResolution(resolution)
            it.setFlashType(ImageCapture.FLASH_TYPE_ONE_SHOT_FLASH)
            it.setFlashMode(_flashMode.value)
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

    fun setZoom(zoom: Float) {
        camera?.cameraControl?.setLinearZoom(zoom)
    }

    fun switchCamera() {
        _lensFacing = when (_lensFacing) {
            CameraLens.FRONT -> {
                if (hasBackCamera) {
                    CameraLens.BACK
                } else {
                    _lensFacing
                }
            }

            CameraLens.BACK -> {
                if (hasFrontCamera) {
                    CameraLens.FRONT
                } else {
                    _lensFacing
                }
            }
        }
        Console.log("Camera Lens Selection: $_lensFacing")
        resetPreview()
    }

    fun setFlashMode(flashMode: FlashMode) {
        if (hasFlash) {
            this._flashMode = flashMode
            capture?.flashMode = flashMode.value
        }
    }

    fun takePicture() {
        val stamp = System.currentTimeMillis()
        val dir = context.getDir(folder, Context.MODE_PRIVATE)
        val file = File(dir, "$stamp.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture?.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                if (detectMotionBlur && detector.isMoving) {
                    notifiers?.onError(CameraError.MOTION_BLUR)
                    file.delete()
                } else {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    val matrix = Matrix()
                    val rotation = getImageRotation();
                    val exif = ExifInterface(file)
                    if (exif.rotationDegrees == rotation) {
                        matrix.postRotate(rotation.toFloat())
                    }
                    val rotated =
                        Bitmap.createBitmap(
                            bitmap, 0, 0,
                            bitmap.width, bitmap.height, matrix, true
                        )
                    val output = if (stampList != null) {
                        val font = context.getString(R.string.calibri_regular)
                        CodePanUtils.stampPhoto(
                            context, rotated, font, 0.035F, stampList, false
                        )
                    } else {
                        rotated
                    }
                    val fos = FileOutputStream(file)
                    output.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    notifiers?.onCapture(file)
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
                    CameraState.Type.OPEN -> {
                        notifiers?.onLoadCamera(this)
                    }

                    else -> {
                        Console.log(cameraState.type)
                    }
                }
            }
            cameraState.error?.let { error ->
                when (error.code) {
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        notifiers?.onError(CameraError.CAMERA_BUSY)
                    }

                    else -> {
                        notifiers?.onError(CameraError.UNABLE_TO_LOAD)
                    }
                }
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

    override val lifecycle: Lifecycle
        get() = lc

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        executor.shutdown()
        lbm.unregisterReceiver(receiver)
    }

    override fun onOrientationChanged(orientation: DeviceOrientation) {
        orientationNotifier?.onOrientationChanged(orientation)
        analysis?.targetRotation = orientation.compensation
        capture?.targetRotation = orientation.compensation
    }

    fun getImageRotation(): Int {
        val orientation = detector.orientation
        return when (_lensFacing) {
            CameraLens.BACK -> {
                return if (orientation.isPortrait) {
                    orientation.degrees
                } else {
                    orientation.opposite
                }
            }

            CameraLens.FRONT -> {
                orientation.opposite
            }
        }
    }
}

