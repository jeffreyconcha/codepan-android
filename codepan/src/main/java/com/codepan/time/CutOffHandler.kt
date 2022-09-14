package com.codepan.time

enum class CutOffType {
    daily,
    weekly,
    monthly,
}

class CutOffHandler(val type: CutOffType, val now: DateTime) {

    private lateinit var _start: DateTime
    private lateinit var _end: DateTime

    val start: DateTime
        get() = _start

    val end: DateTime
        get() = _end

    fun isWithinCutOff(
        start: String,
        end: String,
    ): Boolean {
        val s = now.toCutOff(type, start)
        val e = now.toCutOff(type, end)
        if (s.isBefore(e)) {
            this._start = s
            this._end = e
        } else {
            when (type) {
                CutOffType.daily -> {
                    this._start = s
                    this._end = e.roll(TimeUnit.DAY, 1)
                }
                CutOffType.weekly -> {
                    this._start = s
                    this._end = e.roll(TimeUnit.WEEK, 1)
                }
                CutOffType.monthly -> {
                    this._start = s
                    this._end = e.exactRoll(TimeUnit.MONTH, 1)
                }
            }
        }
        return now.isBetween(_start, _end)
    }
}