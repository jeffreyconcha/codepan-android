package com.codepan.utils

import android.os.Handler
import android.os.Handler.Callback
import android.os.Looper
import android.os.Message
import java.util.*


interface DebouncerNotifier<T> {
    fun onUpdateView()
    fun onRunTask(data: T)
}

class Debouncer<T>(val notifier: DebouncerNotifier<T>, val delay: Long) {
    private var timer: Timer? = null

    constructor(notifier: DebouncerNotifier<T>) : this(notifier, 500L);

    fun run(data: T) {
        timer?.cancel()
        timer = Timer()
        val handler = TaskHandler(data, notifier)
        timer!!.schedule(handler, delay)
    }

    fun cancel() {
        timer?.cancel()
    }
}

private class TaskHandler<T>(val data: T, val notifier: DebouncerNotifier<T>) :
    TimerTask(), Callback {
    override fun run() {
        val handler = Handler(Looper.getMainLooper(), this)
        handler.obtainMessage().sendToTarget()
        notifier.onRunTask(data)
    }

    override fun handleMessage(msg: Message): Boolean {
        notifier.onUpdateView()
        return true
    }
}
