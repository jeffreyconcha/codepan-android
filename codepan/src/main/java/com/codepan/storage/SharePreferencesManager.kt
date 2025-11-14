package com.codepan.storage

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi

class SharedPreferencesManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.packageName,
        Context.MODE_PRIVATE
    )
    private val editor = preferences.edit();

    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String, default: T?): T {
        return when (default) {
            is String -> preferences.getString(key, default)
            is Boolean -> preferences.getBoolean(key, default)
            is Int -> preferences.getInt(key, default)
            is Long -> preferences.getLong(key, default)
            is Float -> preferences.getFloat(key, default)
            else -> preferences.getString(key, null)
        } as T
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    fun <T> setValue(key: String, value: T) {
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
        }
        editor.apply()
    }

    fun clear() {
        editor.clear()
    }

}