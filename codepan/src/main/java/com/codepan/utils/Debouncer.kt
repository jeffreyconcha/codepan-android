package com.codepan.utils

import android.os.Handler
import android.os.Handler.Callback
import android.os.Looper
import android.os.Message
import java.util.*


interface TaskRunner<T> {
    fun run(data: T)
}

interface ViewNotifier {
    fun updateView();
}

class Debouncer<T>(
    val notifier: ViewNotifier?,
    val runner: TaskRunner<T>,
    val delay: Long
) {
    private var timer: Timer? = null

    constructor(runner: TaskRunner<T>) :
        this(null, runner, 500L);

    constructor(notifier: ViewNotifier, runner: TaskRunner<T>) :
        this(notifier, runner, 500L);

    fun run(data: T) {
        timer?.cancel()
        timer = Timer()
        val handler = TaskHandler(data, runner, notifier)
        timer!!.schedule(handler, delay)
    }

    fun cancel() {
        timer?.cancel()
    }
}

private class TaskHandler<T>(
    val data: T,
    val runner: TaskRunner<T>,
    val notifier: ViewNotifier?
) :
    TimerTask(), Callback {
    override fun run() {
        val handler = Handler(Looper.getMainLooper(), this)
        handler.obtainMessage().sendToTarget()
        runner.run(data)
    }

    override fun handleMessage(msg: Message): Boolean {
        notifier?.updateView()
        return true
    }
}
