package com.codepan.time

import android.content.Context
import com.codepan.R

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
        FROM_THE_BEGINNING;

        fun getName(ctx: Context): String {
            return when (this) {
                TODAY -> ctx.getString(R.string.today)
                YESTERDAY -> ctx.getString(R.string.yesterday)
                THIS_WEEK -> ctx.getString(R.string.this_week)
                THIS_MONTH -> ctx.getString(R.string.this_month)
                LAST_WEEK -> ctx.getString(R.string.last_week)
                LAST_MONTH -> ctx.getString(R.string.last_month)
                LAST_7_DAYS -> ctx.getString(R.string.last_7_days)
                LAST_30_DAYS -> ctx.getString(R.string.last_30_days)
                LAST_3_MONTHS -> ctx.getString(R.string.last_3_months)
                LAST_6_MONTHS -> ctx.getString(R.string.last_6_months)
                THIS_YEAR -> ctx.getString(R.string.this_year)
                FROM_THE_BEGINNING -> ctx.getString(R.string.from_the_beginning)
            }
        }
    }

    override fun toString(): String {
        return "${period ?: "Custom"}: ${start.date} - ${end.date}"
    }

    fun getDisplayName(ctx: Context): String {
        return "${period?.getName(ctx) ?: ctx.getString(R.string.custom)} (${start.readableDate} - ${end.readableDate})"
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