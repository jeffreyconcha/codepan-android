package com.codepan.time

import com.codepan.time.TimeUnit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

interface DateTimeFields {
    val dateTime: DateTime
    val date: String
        get() = dateTime.date
    val time: String
        get() = dateTime.time
    val timestamp: Long
        get() = dateTime.timestamp
}

class DateTime(
    val date: String = "0000-00-00",
    val time: String = "00:00:00",
    val timeZone: TimeZone = TimeZone.getDefault(),
) : Comparable<DateTime> {

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
                    return date.time + getOffset()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0L
        }

    val history: String
        get() {
            val current = System.currentTimeMillis()
            val timestamp = this.timestamp
            if (current > timestamp) {
                val difference = current - timestamp
                when {
                    difference > MONTH.milliseconds -> {
                        return "$readableDate at $readableTime"
                    }
                    difference >= WEEK.milliseconds -> {
                        val w = (difference / WEEK.milliseconds).toInt()
                        val type = if (w > 1) "weeks" else "week"
                        return "$w $type ago"
                    }
                    difference >= DAY.milliseconds -> {
                        val d = (difference / DAY.milliseconds).toInt()
                        val type = if (d > 1) "days" else "day"
                        return "$d $type ago"

                    }
                    difference >= HOUR.milliseconds -> {
                        val h = (difference / HOUR.milliseconds).toInt()
                        val type = if (h > 1) "hours" else "hours"
                        return "$h $type ago"
                    }
                    difference >= MINUTE.milliseconds -> {
                        val m = (difference / MINUTE.milliseconds).toInt()
                        val type = if (m > 1) "mins" else "min"
                        return "$m $type ago"
                    }
                }
            }
            return "Just now"
        }

    fun getReadableDate(
        isShort: Boolean = true,
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

    fun getOffset(
        input: TimeZone = TimeZone.getDefault()
    ): Int {
        if (input != timeZone) {
            return input.rawOffset - timeZone.rawOffset
        }
        return 0
    }

    fun isAfter(other: DateTime): Boolean {
        return timestamp > other.timestamp
    }

    fun isBefore(other: DateTime): Boolean {
        return timestamp < other.timestamp
    }

    fun isAfterOrEqual(other: DateTime): Boolean {
        return isAfter(other) || isEqual(other);
    }

    fun isBeforeOrEqual(other: DateTime): Boolean {
        return isBefore(other) || isEqual(other);
    }

    fun isBetween(
        other1: DateTime,
        other2: DateTime
    ): Boolean {
        return if (other1.isBefore(other2)) {
            this.isAfter(other1) && this.isBefore(other2)
        } else {
            this.isAfter(other2) && this.isBefore(other1)
        }
    }

    fun difference(other: DateTime): Long {
        return timestamp - other.timestamp
    }

    fun to(input: TimeZone): DateTime {
        return fromTimestamp(timestamp, input)
    }

    fun trimTime(): DateTime {
        return fromDate(date);
    }

    fun trimDate(): DateTime {
        return fromTime(time);
    }

    fun roll(
        unit: TimeUnit,
        amount: Int
    ): DateTime {
        val total = timestamp + (unit.milliseconds * amount)
        return fromTimestamp(total, timeZone)
    }

    fun isEqual(other: DateTime): Boolean {
        return timestamp == other.timestamp
    }

    fun isNotEqual(other: DateTime): Boolean {
        return timestamp != other.timestamp
    }

    override fun toString(): String {
        val offset = timeZone.rawOffset / HOUR.milliseconds
        val sign = if (offset >= 0) "+" else "-"
        val timeZone = "${timeZone.id} $sign${abs(offset)}:00"
        return "DateTime(date = $date, time = $time, timeZone = $timeZone)"
    }

    override fun compareTo(other: DateTime): Int {
        val difference = timestamp - other.timestamp
        return (difference / 1000L).toInt()
    }

    companion object {
        fun now(): DateTime {
            val cal = Calendar.getInstance()
            return fromCalendar(cal)
        }

        fun today(): DateTime {
            val cal = Calendar.getInstance()
            return fromCalendar(cal).trimTime()
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