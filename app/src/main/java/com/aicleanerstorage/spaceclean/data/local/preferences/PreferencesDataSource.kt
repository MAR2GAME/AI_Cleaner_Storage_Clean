package com.aicleanerstorage.spaceclean.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * SharedPreferences 的数据源。
 *
 * 只暴露读写原语，不定义业务 key；业务 key 与默认值留在
 * `DefaultAppSettingsRepository`，避免存储细节泄漏到 domain。
 *
 * @param context 用于取得 SharedPreferences 的上下文。
 * @param fileName 偏好文件名；默认值与旧版本保持一致。
 */
internal class PreferencesDataSource(
    context: Context,
    fileName: String = "quick_clean_settings",
) {
    private val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun getString(
        key: String,
        defaultValue: String,
    ): String = preferences.getString(key, defaultValue) ?: defaultValue

    fun getStringOrNull(key: String): String? = preferences.getString(key, null)

    fun contains(key: String): Boolean = preferences.contains(key)

    fun getBoolean(
        key: String,
        defaultValue: Boolean,
    ): Boolean = preferences.getBoolean(key, defaultValue)

    fun getLong(
        key: String,
        defaultValue: Long,
    ): Long = preferences.getLong(key, defaultValue)

    fun getStringSet(
        key: String,
        defaultValue: Set<String>,
    ): Set<String> = preferences.getStringSet(key, defaultValue).orEmpty()

    fun edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit,
    ) {
        preferences.edit(commit = commit, action = action)
    }

    /**
     * 同步写入并返回系统提交结果。
     *
     * @param action 对编辑器的修改。
     * @return true 表示写入磁盘成功；false 表示系统拒绝提交。
     */
    fun editAndCommit(action: SharedPreferences.Editor.() -> Unit): Boolean = preferences.edit().apply(action).commit()
}
