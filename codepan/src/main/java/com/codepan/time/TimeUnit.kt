package com.codepan.time

import java.util.*

enum class TimeUnit(
    val milliseconds: Long,
    val id: Int,
) {
    MILLISECOND(1L, Calendar.MILLISECOND),
    SECOND(1000L, Calendar.SECOND),
    MINUTE(SECOND.milliseconds * 60, Calendar.MINUTE),
    HOUR(MINUTE.milliseconds * 60, Calendar.HOUR_OF_DAY),
    DAY(HOUR.milliseconds * 24, Calendar.DAY_OF_YEAR),
    WEEK(DAY.milliseconds * 7, Calendar.WEEK_OF_YEAR),
    MONTH(DAY.milliseconds * 30, Calendar.MONTH),
    YEAR(DAY.milliseconds * 365, Calendar.YEAR),
}