package com.codepan.utils

import java.util.*

class Debouncer(val delay: Long = 500L) {
    private val timer = Timer()

    fun run(task: TimerTask) {
        timer.cancel()
        timer.schedule(task, delay);
    }

    fun cancel() {
        timer.cancel()
    }
}
