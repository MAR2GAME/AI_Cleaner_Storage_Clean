package com.aicleanerstorage.spaceclean.base.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesUtil(context: Context, fileName: String = "quick_clean_settings") {
    private val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String): String =
        preferences.getString(key, defaultValue) ?: defaultValue

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = preferences.getBoolean(key, defaultValue)

    fun getLong(key: String, defaultValue: Long): Long = preferences.getLong(key, defaultValue)

    fun getStringSet(key: String, defaultValue: Set<String>): Set<String> =
        preferences.getStringSet(key, defaultValue).orEmpty()

    fun edit(commit: Boolean = false, action: SharedPreferences.Editor.() -> Unit) {
        preferences.edit(commit = commit, action = action)
    }
}
