package com.codepan.time

import com.codepan.time.TimeUnit.*
import com.codepan.utils.CodePanUtils
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

class DateTimePattern {
    companion object {
        const val displayDate = "MMMM d, yyyy"
        const val abbrDate = "MMM d, yyyy"
        const val displayMonth: String = "MMMM"
        const val abbrMonth: String = "MMM"
        const val year: String = "yyyy"
        const val displayMonthYear: String = "MMMM yyyy"
        const val abbrMonthYear: String = "MMM yyyy"
        const val weekday = "EEEE"
        const val abbrWeekday = "EEE"
        const val displayTime = "h:mm a"
        const val displayFullTime = "h:mm:ss a"
        const val displayDateTime = "$displayDate, $displayTime"
        const val displayTimeDate = "$displayTime, $displayDate"
        const val abbrDateTime = "$abbrDate, $displayTime"
        const val displayWeekdayDate = "$weekday, $displayDate"
        const val abbrWeekdayDate = "$abbrWeekday, $abbrDate"
    }
}

class DayOfWeek {

    enum class Weekday(val value: Int) {
        MONDAY(1),
        TUESDAY(2),
        WEDNESDAY(3),
        THURSDAY(4),
        FRIDAY(5),
        SATURDAY(6),
        SUNDAY(7),
    }

    companion object {
        fun fromName(name: String): Weekday? {
            for (weekday in Weekday.entries) {
                if (weekday.name.lowercase().startsWith(name.lowercase())) {
                    return weekday
                }
            }
            return null
        }

        fun fromValue(value: Int): Weekday? {
            for (weekday in Weekday.entries) {
                if (weekday.value == value) {
                    return weekday
                }
            }
            return null
        }
    }
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


    val offsetHours: String
        get() {
            val offset = timeZone.rawOffset / HOUR.milliseconds
            val sign = if (offset >= 0) "+" else "-"
            return "$sign${abs(offset)}:00"
        }

    val weekday: DayOfWeek.Weekday
        get() {
            val cal = toCalendar()
            val intValue = cal.get(Calendar.DAY_OF_WEEK) - 1
            val dayOfWeek = if (intValue == 0) 7 else intValue
            return DayOfWeek.fromValue(dayOfWeek)!!
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
                        val type = if (h > 1) "hours" else "hour"
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

    fun format(pattern: String): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(Date(timestamp))
    }

    fun getOffset(
        input: TimeZone = TimeZone.getDefault(),
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
        return isAfter(other) || isEqual(other)
    }

    fun isBeforeOrEqual(other: DateTime): Boolean {
        return isBefore(other) || isEqual(other)
    }

    fun isBetween(
        other1: DateTime,
        other2: DateTime,
    ): Boolean {
        return if (other1.isBefore(other2)) {
            this.isAfterOrEqual(other1) && this.isBeforeOrEqual(other2)
        } else {
            this.isAfterOrEqual(other2) && this.isBeforeOrEqual(other1)
        }
    }

    fun difference(other: DateTime): Long {
        return timestamp - other.timestamp
    }

    fun to(input: TimeZone): DateTime {
        return fromTimestamp(timestamp, input)
    }

    fun trimTime(): DateTime {
        return fromDate(date)
    }

    fun trimDate(): DateTime {
        return fromTime(time)
    }

    fun roll(
        unit: TimeUnit,
        amount: Int,
    ): DateTime {
        val total = timestamp + (unit.milliseconds * amount)
        return fromTimestamp(total, timeZone)
    }

    fun exactRoll(
        unit: TimeUnit,
        amount: Int,
    ): DateTime {
        val cal = toCalendar()
        cal.add(unit.id, amount);
        return fromCalendar(cal)
    }

    fun isEqual(other: DateTime): Boolean {
        return timestamp == other.timestamp
    }

    fun isNotEqual(other: DateTime): Boolean {
        return timestamp != other.timestamp
    }

    fun isZero(): Boolean {
        val offset = getOffset().toLong()
        return timestamp - offset <= 0L
    }

    fun isNotZero(): Boolean {
        return !isZero()
    }

    override fun toString(): String {
        val timeZone = "${timeZone.id} $offsetHours"
        return "DateTime(date = $date, time = $time, timeZone = $timeZone)"
    }

    override fun compareTo(other: DateTime): Int {
        val difference = timestamp - other.timestamp
        return (difference / 1000L).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other is DateTime) {
            return timestamp == other.timestamp
        }
        return false
    }

    fun toCutOff(type: CutOffType, value: String): DateTime {
        when (type) {
            CutOffType.daily -> {
                if (value.split(":").size == 3) {
                    return DateTime(date, value, timeZone)
                }
                throw Exception("Invalid time format, value must be a 24hr format (00:00:00).")
            }

            CutOffType.weekly -> {
                val weekday = DayOfWeek.fromName(value)
                if (weekday != null) {
                    for (i in (0..6)) {
                        val d1 = this.roll(DAY, -i);
                        val d2 = this.roll(DAY, i);
                        if (d1.weekday == weekday) {
                            return d1;
                        }
                        if (d2.weekday == weekday) {
                            return d2;
                        }
                    }
                }
                throw Exception("Invalid weekday name.")
            }

            CutOffType.monthly -> {
                val day = value.toInt()
                if (day <= 28) {
                    val formatted = String.format(Locale.ENGLISH, "%02d", day)
                    val elements = date.split("-")
                    val newDate = "${elements[0]}-${elements[1]}-$formatted"
                    return DateTime(newDate, time, timeZone)
                }
                throw Exception("Monthly cut-off value must not exceed the 28th day.")
            }
        }
    }

    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.timeZone = timeZone
        return cal
    }

    fun daysElapsedInMonth(): Int {
        val cal = toCalendar()
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun getNoOfDaysInMonth(): Int {
        val cal = toCalendar()
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun toFirstDayOfThisWeek(): DateTime {
        for (d in 0..6) {
            val date = exactRoll(DAY, -d)
            if (date.weekday.value == DayOfWeek.Weekday.MONDAY.value) {
                return date;
            }
        }
        return this;
    }

    fun toLastDayOfThisWeek(): DateTime {
        for (d in 0..6) {
            val date = exactRoll(DAY, d)
            if (date.weekday.value == DayOfWeek.Weekday.SUNDAY.value) {
                return date;
            }
        }
        return this;
    }

    fun toFirstDayOfMonth(): DateTime {
        val cal = toCalendar()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return fromCalendar(cal)
    }

    fun toLastDayOfMonth(): DateTime {
        val cal = toCalendar()
        val max = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, max)
        return fromCalendar(cal)
    }

    fun toFirstDayOfYear(): DateTime {
        val cal = toCalendar()
        val min = cal.getActualMinimum(Calendar.DAY_OF_YEAR)
        cal.set(Calendar.DAY_OF_YEAR, min)
        return fromCalendar(cal)
    }

    fun toLastDayOfYear(): DateTime {
        val cal = toCalendar()
        val max = cal.getActualMaximum(Calendar.DAY_OF_YEAR)
        cal.set(Calendar.DAY_OF_YEAR, max)
        return fromCalendar(cal)
    }

    fun toPeriod(period: DateTimeRange.Period): DateTimeRange {
        return when (period) {
            DateTimeRange.Period.TODAY -> {
                DateTimeRange.today()
            }

            DateTimeRange.Period.YESTERDAY -> {
                DateTimeRange.yesterday()
            }

            DateTimeRange.Period.THIS_WEEK -> {
                DateTimeRange(
                    start = toFirstDayOfThisWeek(),
                    end = toLastDayOfThisWeek(),
                    period = DateTimeRange.Period.THIS_WEEK
                )
            }

            DateTimeRange.Period.THIS_MONTH -> {
                DateTimeRange(
                    start = toFirstDayOfMonth(),
                    end = toLastDayOfMonth(),
                    period = DateTimeRange.Period.THIS_MONTH
                )
            }

            DateTimeRange.Period.LAST_WEEK -> {
                val date = exactRoll(DAY, -7);
                val start = date.toFirstDayOfThisWeek()
                DateTimeRange(
                    start = start,
                    end = start.toLastDayOfThisWeek(),
                    period = DateTimeRange.Period.LAST_WEEK
                )
            }

            DateTimeRange.Period.LAST_MONTH -> {
                val date = exactRoll(MONTH, -1);
                val start = date.toFirstDayOfMonth()
                DateTimeRange(
                    start = start,
                    end = start.toLastDayOfMonth(),
                    period = DateTimeRange.Period.LAST_MONTH
                )
            }

            DateTimeRange.Period.LAST_7_DAYS -> {
                DateTimeRange(
                    start = exactRoll(DAY, -6),
                    end = this,
                    period = DateTimeRange.Period.LAST_7_DAYS
                )
            }

            DateTimeRange.Period.LAST_30_DAYS -> {
                DateTimeRange(
                    start = exactRoll(DAY, -30),
                    end = this,
                    period = DateTimeRange.Period.LAST_30_DAYS
                )
            }

            DateTimeRange.Period.LAST_3_MONTHS -> {
                DateTimeRange(
                    start = exactRoll(MONTH, -3),
                    end = this,
                    period = DateTimeRange.Period.LAST_3_MONTHS
                )
            }

            DateTimeRange.Period.LAST_6_MONTHS -> {
                DateTimeRange(
                    start = exactRoll(MONTH, -6),
                    end = this,
                    period = DateTimeRange.Period.LAST_6_MONTHS
                )
            }

            DateTimeRange.Period.THIS_YEAR -> {
                DateTimeRange(
                    start = toFirstDayOfYear(),
                    end = toLastDayOfYear(),
                    period = DateTimeRange.Period.THIS_YEAR
                )
            }

            DateTimeRange.Period.FROM_THE_BEGINNING -> {
                DateTimeRange(
                    start = fromDate("1970-01-01"),
                    end = this,
                    period = DateTimeRange.Period.FROM_THE_BEGINNING
                )
            }
        }
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

        fun yesterday(): DateTime {
            return today().roll(DAY, -1)
        }

        fun nowIn(
            timeZone: TimeZone,
        ): DateTime {
            val cal = Calendar.getInstance()
            cal.timeZone = timeZone
            return fromCalendar(cal)
        }

        fun fromDate(
            date: String,
        ): DateTime {
            return DateTime(
                date = date,
            )
        }

        fun fromTime(
            time: String,
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
            timeZone: TimeZone = TimeZone.getDefault(),
        ): DateTime {
            val cal = Calendar.getInstance()
            cal.timeZone = timeZone
            cal.timeInMillis = timestamp
            return fromCalendar(cal)
        }
    }
}