package com.flamyoad.tsukiviewer

import android.content.Context
import android.content.SharedPreferences

class MyAppPreference(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        @Volatile private var INSTANCE: MyAppPreference? = null

        fun getInstance(context: Context): MyAppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = MyAppPreference(context)

                INSTANCE = instance
                instance // return instance
            }
        }
    }

    fun put(key: String, value: String) {
        prefs.edit()
            .putString(key, value)
            .apply()
    }

    fun put(key: String, value: Int) {
        prefs.edit()
            .putInt(key, value)
            .apply()
    }

    fun put(key: String, value: Boolean) {
        prefs.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun put(key: String, value: Float) {
        prefs.edit()
            .putFloat(key, value)
            .apply()
    }

    fun put(key: String, value: Long) {
        prefs.edit()
            .putLong(key, value)
            .apply()
    }
}