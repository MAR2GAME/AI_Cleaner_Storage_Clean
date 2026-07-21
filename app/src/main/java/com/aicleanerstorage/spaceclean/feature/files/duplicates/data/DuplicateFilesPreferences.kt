package com.aicleanerstorage.spaceclean.feature.files.duplicates.data

interface DuplicateFilesPreferences {
    fun isWarningAccepted(): Boolean

    fun acceptWarning()
}
