package com.codepan.utils

import java.util.*

class Debouncer(val delay: Long = 500L) {
    private var timer: Timer? = null;

    fun run(task: TimerTask) {
        timer?.cancel()
        timer = Timer();
        timer!!.schedule(task, delay);
    }

    fun cancel() {
        timer?.cancel()
    }
}
