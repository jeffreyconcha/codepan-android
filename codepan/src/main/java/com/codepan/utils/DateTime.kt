package com.codepan.utils

import org.intellij.lang.annotations.Pattern
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class DateTime(
    val date: String = "0000-00-00",
    val time: String = "00:00:00"
) {

    private val pattern = "yyyy-MM-dd HH:mm:ss";
    private val locale = Locale.ENGLISH

    val timestamp: Long
        get() {
            try {
                val formatter = SimpleDateFormat(pattern, locale)
                val date = formatter.parse("$date $time");
                if (date != null) {
                    return date.time
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return 0L
        }

    fun getReadableDate(
        isShort: Boolean = false,
        withYear: Boolean = false,
        withDay: Boolean = false,
    ): String {
        var pattern = "EEE, MMMM d, yyyy";
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

    companion object {

        fun fromDate(date: String): DateTime {
            return DateTime(
                date = date,
            );
        }

        fun fromTime(time: String): DateTime {
            return DateTime(
                time = time,
            );
        }

        fun now(): DateTime {
            val cal = Calendar.getInstance();
            return DateTime(
                date = String.format(Locale.ENGLISH, "%tF", cal),
                time = String.format(Locale.ENGLISH, "%tT", cal),
            );
        }
    }
}