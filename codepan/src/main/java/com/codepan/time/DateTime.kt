package com.codepan.time

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class DateTime(
    val date: String = "0000-00-00",
    val time: String = "00:00:00",
    val timeZone: TimeZone = TimeZone.getDefault(),
) {

    private val pattern = "yyyy-MM-dd HH:mm:ss"
    private val locale = Locale.ENGLISH

    /**
     * @return the date in the current timezone.
     */
    val readableDate: String
        get() = getReadableDate()

    /**
     * @return the time in the current timezone.
     */
    val readableTime: String
        get() = getReadableTime()

    val timestamp: Long
        get() {
            try {
                val formatter = SimpleDateFormat(pattern, locale)
                val date = formatter.parse("$date $time")
                if (date != null) {
                    return date.time + timeZoneOffset
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0L
        }

    val timeZoneOffset: Int
        get() {
            val local = TimeZone.getDefault()
            if (local != timeZone) {
                return local.rawOffset - timeZone.rawOffset
            }
            return 0
        }

    fun getReadableDate(
        isShort: Boolean = false,
        withYear: Boolean = true,
        withDay: Boolean = false,
    ): String {
        var pattern = "EEE, MMMM d, yyyy"
        if (isShort) {
            pattern = pattern.replace("MMMM", "MMM")
        }
        if (!withYear) {
            pattern = pattern.replace(", yyyy", "")
        }
        if (!withDay) {
            pattern = pattern.replace("EEE, ", "")
        }
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(Date(timestamp))
    }

    fun getReadableTime(withSeconds: Boolean = false): String {
        var pattern = "h:mm:ss a"
        if (!withSeconds) {
            pattern = pattern.replace(":ss", "")
        }
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(Date(timestamp))
    }

    override fun toString(): String {
        val offset = timeZone.rawOffset / 3600000;
        val sign = if (offset >= 0) "+" else "-";
        val timeZone = "${timeZone.id} $sign${abs(offset)}:00";
        return "DateTime(date = $date, time = $time, timeZone = $timeZone)";
    }

    companion object {

        fun now(): DateTime {
            val cal = Calendar.getInstance()
            return fromCalendar(cal)
        }

        fun nowIn(
            timeZone: TimeZone
        ): DateTime {
            val cal = Calendar.getInstance()
            cal.timeZone = timeZone
            return fromCalendar(cal)
        }

        fun fromDate(
            date: String
        ): DateTime {
            return DateTime(
                date = date,
            )
        }

        fun fromTime(
            time: String
        ): DateTime {
            return DateTime(
                time = time,
            )
        }

        fun fromCalendar(
            cal: Calendar,
        ): DateTime {
            return DateTime(
                date = String.format(Locale.ENGLISH, "%tF", cal),
                time = String.format(Locale.ENGLISH, "%tT", cal),
                timeZone = cal.timeZone,
            )
        }

        fun fromTimestamp(
            timestamp: Long,
            timeZone: TimeZone = TimeZone.getDefault()
        ): DateTime {
            val cal = Calendar.getInstance()
            cal.timeZone = timeZone
            cal.timeInMillis = timestamp
            return fromCalendar(cal)
        }
    }
}