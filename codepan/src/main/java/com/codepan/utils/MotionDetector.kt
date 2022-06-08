package com.codepan.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.codepan.utils.Console
import kotlin.math.abs

const val DEFAULT_SENSITIVITY = 0.5F;
const val DEFAULT_ALLOWANCE = 300L;

class MotionDetector(
    val context: Context,
    val sensitivity: Float = DEFAULT_SENSITIVITY,
    val allowanceTime: Long = DEFAULT_ALLOWANCE
) : SensorEventListener {

    private val sm: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor: Sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var motionUpdate: Long = 0L;
    private var sensorUpdate: Long = 0L;
    private var x: Float = 0F;
    private var y: Float = 0F;
    private var z: Float = 0F;

    val isMoving: Boolean
        get() {
            val elapsed = SystemClock.elapsedRealtime();
            val difference = elapsed - motionUpdate;
            return difference <= allowanceTime
        }

    constructor(context: Context) :
        this(context, DEFAULT_SENSITIVITY, DEFAULT_ALLOWANCE)

    init {
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val elapsed = SystemClock.elapsedRealtime()
        val cx = event?.values?.get(0) ?: 0F
        val cy = event?.values?.get(1) ?: 0F
        val cz = event?.values?.get(2) ?: 0F
        val dx = abs(cx - x);
        val dy = abs(cy - y);
        val dz = abs(cz - z);
        if (dx >= sensitivity || dy >= sensitivity || dz >= sensitivity) {
            motionUpdate = elapsed;
        }
        x = cx
        y = cy
        z = cz
        sensorUpdate = elapsed
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun dispose() {
        sm.unregisterListener(this, sensor)
    }
}