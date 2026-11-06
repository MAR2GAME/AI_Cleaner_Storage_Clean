package com.aicleanerstorage.spaceclean.app.storage

import com.aicleanerstorage.spaceclean.feature.files.domain.duplicates.DuplicateFilesPreferences


import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.AppPreferences

class DuplicateFilesPreferencesImpl(
    appPreferences: AppPreferences,
) : DuplicateFilesPreferences {
    private val store = appPreferences.store

    override fun isWarningAccepted(): Boolean =
        store.getBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, false)

    override fun acceptWarning() {
        store.edit().putBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, true).commit()
    }
}

private const val KEY_DUPLICATE_FILES_WARNING_ACCEPTED = "duplicate_files_warning_accepted"
