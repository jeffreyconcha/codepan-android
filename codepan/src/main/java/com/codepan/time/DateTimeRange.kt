package com.codepan.time

class DateTimeRange(
    val start: DateTime,
    val end: DateTime,
    val period: Period?,
) {

    enum class Period {
        TODAY,
        YESTERDAY,
        THIS_WEEK,
        THIS_MONTH,
        LAST_WEEK,
        LAST_MONTH,
        LAST_7_DAYS,
        LAST_30_DAYS,
        LAST_3_MONTHS,
        LAST_6_MONTHS,
        THIS_YEAR,
        FROM_THE_BEGINNING,
    }

    override fun toString(): String {
        return "$period: ${start.date} - ${end.date}"
    }

    companion object {

        fun today(): DateTimeRange {
            val today = DateTime.today()
            return DateTimeRange(today, today, Period.TODAY)
        }

        fun yesterday(): DateTimeRange {
            val yesterday = DateTime.yesterday()
            return DateTimeRange(yesterday, yesterday, Period.YESTERDAY)
        }
    }
}