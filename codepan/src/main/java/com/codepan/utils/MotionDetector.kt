package com.codepan.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlin.math.*

const val DEFAULT_SENSITIVITY = 0.5F
const val DEFAULT_ALLOWANCE = 300L

enum class DeviceOrientation(val degrees: Int) {
    PORTRAIT_90(90),
    LANDSCAPE_0(0),
    LANDSCAPE_180(180),
    PORTRAIT_270(270),
}

class MotionDetector(
    val context: Context,
    val sensitivity: Float = DEFAULT_SENSITIVITY,
    val allowanceTime: Long = DEFAULT_ALLOWANCE
) : SensorEventListener {

    private val sm: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var motionUpdate: Long = 0L
    private var sensorUpdate: Long = 0L
    private var x: Float = 0F
    private var y: Float = 0F
    private var z: Float = 0F
    private var rotation: Int = 0

    val isMoving: Boolean
        get() {
            val elapsed = SystemClock.elapsedRealtime()
            val difference = elapsed - motionUpdate
            return difference <= allowanceTime
        }

    val orientation: DeviceOrientation
        get() {
            return when (rotation) {
                in -60..60 -> DeviceOrientation.PORTRAIT_90
                in -180..-120 -> DeviceOrientation.PORTRAIT_270
                in 120..180 -> DeviceOrientation.PORTRAIT_270
                in -119..-61 -> DeviceOrientation.LANDSCAPE_0
                in 61..119 -> DeviceOrientation.LANDSCAPE_180
                else -> DeviceOrientation.PORTRAIT_90
            }
        }

    constructor(context: Context) :
        this(context, DEFAULT_SENSITIVITY, DEFAULT_ALLOWANCE)

    init {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val elapsed = SystemClock.elapsedRealtime()
                val cx = event.values?.get(0) ?: 0F
                val cy = event.values?.get(1) ?: 0F
                val cz = event.values?.get(2) ?: 0F
                val dx = abs(cx - x)
                val dy = abs(cy - y)
                val dz = abs(cz - z)
                if (dx >= sensitivity || dy >= sensitivity || dz >= sensitivity) {
                    motionUpdate = elapsed
                }
                x = cx
                y = cy
                z = cz
                sensorUpdate = elapsed
                val g = event.values.clone();
                val vector = sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);
                g[0] /= vector
                g[1] /= vector
                g[2] /= vector
                val inclination = Math.toDegrees(acos(g[2]).toDouble()).roundToInt()
                if (inclination in 26..154) {
                    rotation = Math.toDegrees(atan2(g[0], g[1]).toDouble()).roundToInt()
                    Console.log("Orientation: $orientation @$rotation")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun dispose() {
        sm.unregisterListener(this, accelerometer)
    }
}