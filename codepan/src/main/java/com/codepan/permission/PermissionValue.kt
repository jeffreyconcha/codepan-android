package com.codepan.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

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
        val receiveSms = PermissionValue(
            Manifest.permission.RECEIVE_SMS
        );
        val sendSms = PermissionValue(
            Manifest.permission.SEND_SMS
        );
        val sms = PermissionValue(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
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