package com.codepan.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import com.codepan.app.CPFragmentActivity

enum class PermissionType(val value: PermissionValue) {
    @RequiresApi(Build.VERSION_CODES.Q)
    BACKGROUND_LOCATION(PermissionValue.backgroundLocation),
    FOREGROUND_LOCATION(PermissionValue.foregroundLocation),
    FILES_AND_MEDIA(PermissionValue.filesAndMedia),
    CAMERA(PermissionValue.camera),
    MICROPHONE(PermissionValue.microphone),
    PHONE_NUMBERS(PermissionValue.phoneNumbers),
    READ_CALL_LOG(PermissionValue.readCallLog),
    READ_SMS(PermissionValue.readSms),
}

class PermissionValue(vararg values: String) {

    val permissions = values

    companion object {
        @RequiresApi(Build.VERSION_CODES.Q)
        val backgroundLocation = PermissionValue(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        );
        val foregroundLocation = PermissionValue(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val filesAndMedia = PermissionValue(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val camera = PermissionValue(
            Manifest.permission.CAMERA
        );
        val microphone = PermissionValue(
            Manifest.permission.RECORD_AUDIO
        );
        val readCallLog = PermissionValue(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
        );
        val readSms = PermissionValue(
            Manifest.permission.READ_SMS
        );
        val phoneNumbers: PermissionValue
            get() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return PermissionValue(Manifest.permission.READ_PHONE_NUMBERS);
                }
                return PermissionValue(Manifest.permission.READ_PHONE_STATE);
            }
    }
}

interface PermissionEvents {

    val handler: PermissionHandler

    fun onPermissionsResult(
        handler: PermissionHandler,
        isGranted: Boolean,
    )

    fun onShowPermissionRationale(
        handler: PermissionHandler,
        permission: PermissionType,
    )
}

class PermissionHandler(
    val activity: CPFragmentActivity,
    val callback: PermissionEvents,
    vararg permissions: PermissionType
) : OnRequestPermissionsResultCallback {

    val types = permissions

    val isGranted: Boolean
        get() = isGranted(activity, *types)

    init {
        activity.setOnRequestPermissionsResultCallback(this)
    }

    companion object {
        const val REQUEST_CODE = 1

        fun isGranted(context: Context, vararg types: PermissionType): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (type in types) {
                    for (permission in type.value.permissions) {
                        when (context.checkSelfPermission(permission)) {
                            PackageManager.PERMISSION_DENIED ->
                                return false;
                            PackageManager.PERMISSION_GRANTED -> {
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    fun goToSettings() {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:${activity.packageName}")
        activity.setHandler(this)
        activity.startActivity(intent)
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val denied = arrayListOf<String>()
            var hasRational = false
            types@ for (type in types) {
                for (permission in type.value.permissions) {
                    when (activity.checkSelfPermission(permission)) {
                        PackageManager.PERMISSION_GRANTED -> {
                        }
                        PackageManager.PERMISSION_DENIED -> {
                            denied.add(permission)
                            if (activity.shouldShowRequestPermissionRationale(permission)) {
                                callback.onShowPermissionRationale(this, type)
                                hasRational = true
                                break@types
                            }
                        }
                    }
                }
            }
            if (denied.isNotEmpty()) {
                if (!hasRational) {
                    activity.requestPermissions(denied.toTypedArray(), REQUEST_CODE)
                }
            }
            else {
                callback.onPermissionsResult(this, true)
            }
        }
        else {
            callback.onPermissionsResult(this, true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty()) {
            var isGranted = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isGranted = false
                    break
                }

            }
            callback.onPermissionsResult(this, isGranted)
        }
    }
}