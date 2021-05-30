package com.codepan.telephony

import android.Manifest
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.codepan.permission.PermissionHandler
import com.codepan.permission.PermissionType
import com.codepan.storage.SharedPreferencesManager

private interface SmsNotifier {
    fun onSmsChanged(selfChange: Boolean)
}

private class SmsObserver(val callback: SmsNotifier, handler: Handler?) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        callback.onSmsChanged(selfChange)
    }
}

interface SmsEvents {
    fun onSmsReceived(data: SmsData)
    fun onSmsSent(data: SmsData)
}

data class SmsData(
    val id: Int,
    val threadId: Int,
    val number: String,
    val message: String,
    val timestamp: Long,
)

class SmsEventManager(val context: Context) : SmsNotifier {

    private val preferences = SharedPreferencesManager(context)
    private val uri = Uri.parse("content://sms")
    private val resolver = context.contentResolver
    private lateinit var observer: SmsObserver
    private var callback: SmsEvents? = null
    private val handler = Handler(Looper.getMainLooper())

    private var lastId: Int = 0
        get() = preferences.getValue(LAST_SMS_ID, 0)
        set(value) {
            if (field != value) {
                preferences.setValue(LAST_SMS_ID, value)
                field = value
            }
        }

    @RequiresPermission(Manifest.permission.READ_SMS)
    fun registerObserver(callback: SmsEvents) {
        observer = SmsObserver(this, handler)
        resolver.registerContentObserver(uri, true, observer)
        this.callback = callback;
    }

    fun unregisterObserver() {
        resolver.unregisterContentObserver(observer)
    }

    companion object {
        const val LAST_SMS_ID: String = "last_sms_id"
    }

    override fun onSmsChanged(selfChange: Boolean) {
        if (PermissionHandler.isGranted(context, PermissionType.READ_SMS)) {
            val cursor = resolver.query(uri, null, null,
                null, null)?.also {
                if (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndex("_id"))
                    if (id > lastId) {
                        val protocol = it.getString(it.getColumnIndex("protocol"))
                        val type = it.getInt(it.getColumnIndex("type"))
                        val data = SmsData(
                            id = id,
                            threadId = it.getInt(it.getColumnIndex("thread_id")),
                            number = it.getString(it.getColumnIndex("address")),
                            message = it.getString(it.getColumnIndex("body")),
                            timestamp = it.getLong(it.getColumnIndex("date")),
                        )
                        if (protocol == null && type == 2) {
                            callback?.onSmsSent(data)
                        }
                        else {
                            callback?.onSmsReceived(data)
                        }
                    }
                    lastId = id
                }
            }
            cursor?.close()
        }
    }


}