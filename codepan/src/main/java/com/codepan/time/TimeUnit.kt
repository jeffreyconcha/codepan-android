package com.codepan.time

enum class TimeUnit(val milliseconds: Long) {
    MILLISECOND(1L),
    SECOND(1000L),
    MINUTE(SECOND.milliseconds * 60),
    HOUR(MINUTE.milliseconds * 60),
    DAY(HOUR.milliseconds * 24),
    WEEK(DAY.milliseconds * 7),
    MONTH(DAY.milliseconds * 30),
    YEAR(DAY.milliseconds * 365),
}