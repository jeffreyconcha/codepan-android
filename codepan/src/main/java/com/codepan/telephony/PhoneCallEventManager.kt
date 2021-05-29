package com.codepan.telephony

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.CallLog
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import com.codepan.permission.PermissionHandler
import com.codepan.permission.PermissionType
import com.codepan.utils.Console
import java.security.Permission

enum class PhoneCallType {
    INCOMING,
    OUTGOING,
}

interface PhoneCallEvents {

    /**
     * Unfortunately this only triggers for INCOMING call.
     * @param type Type of phone call INCOMING or OUTGOING
     */
    fun onPhoneCallRinging(type: PhoneCallType)

    /**
     * Triggers upon accepting the call.<br/>
     * This is only accurate for all INCOMING calls since OUTGOING call
     * does not trigger the onCallRinging callback?.
     * @param type The type of phone call INCOMING or OUTGOING
     */
    fun onPhoneCallStarted(type: PhoneCallType)

    /**
     * Triggers upon finishing a successful call.
     * @param type The type of phone call INCOMING or OUTGOING
     * @param data The data of the last logged call.
     */
    fun onPhoneCallFinished(type: PhoneCallType, data: PhoneCallData)

    /**
     * Triggers if the call has not been answered.
     * @param type Type of phone call INCOMING or OUTGOING
     * @param data The data of the last logged call.
     */
    fun onPhoneCallMissed(type: PhoneCallType, data: PhoneCallData)
}

data class PhoneCallData(
    val number: String,
    val duration: Long,
    val status: Int,
    val timestamp: Long,
)

class PhoneCallEventManager(val context: Context) {

    private val resolver = context.contentResolver
    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE
    private var callback: PhoneCallEvents? = null
    private var callType: PhoneCallType? = null
    private var _timestamp: Long = 0L

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    fun registerObserver(callback: PhoneCallEvents) {
        this.callback = callback;
    }

    private val lastCallLog: PhoneCallData?
        @SuppressLint("MissingPermission", "Recycle")
        get() {
            resolver.query(CallLog.Calls.CONTENT_URI, null, null,
                null, null)?.also {
                if (it.moveToLast()) {
                    val timestamp = if (_timestamp != 0L) {
                        _timestamp
                    }
                    else {
                        it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                    }
                    val data = PhoneCallData(
                        number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)),
                        duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION)),
                        status = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE)),
                        timestamp = timestamp,
                    )
                    it.close()
                    return data
                }
                else {
                    it.close()
                }
            }
            return null
        }

    /**
     * Call this every time the broadcast android.intent.action.PHONE_STATE is triggered
     * @param intent data from the onReceived
     * @see android.content.BroadcastReceiver
     */
    fun updateEvent(intent: Intent) {
        if (PermissionHandler.isGranted(context, PermissionType.READ_CALL_LOG)) {
            if (intent.hasExtra(TelephonyManager.EXTRA_STATE)) {
                val state = when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                    TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
                    TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
                    else -> TelephonyManager.CALL_STATE_IDLE
                }
                onCallStateChanged(state)
            }
        }
    }

    private fun onCallStateChanged(state: Int) {
        if (state != lastState) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    callback?.onPhoneCallRinging(PhoneCallType.INCOMING)
                    _timestamp = System.currentTimeMillis()
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    callType = when (lastState) {
                        TelephonyManager.CALL_STATE_RINGING -> PhoneCallType.INCOMING
                        TelephonyManager.CALL_STATE_IDLE -> PhoneCallType.OUTGOING
                        else -> null
                    }
                    if (callType != null) {
                        callback?.onPhoneCallStarted(callType!!)
                    }
                    _timestamp = System.currentTimeMillis()

                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    lastCallLog?.also { data ->
                        when (lastState) {
                            TelephonyManager.CALL_STATE_RINGING ->
                                callback?.onPhoneCallMissed(PhoneCallType.INCOMING, data)
                            else -> {
                                callType?.also { type ->
                                    if (data.duration != 0L) {
                                        callback?.onPhoneCallFinished(type, data)
                                    }
                                    else {
                                        callback?.onPhoneCallMissed(type, data)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.lastState = state;
    }
}