package com.aicleanerstorage.spaceclean.feature.files.domain.duplicates

interface DuplicateFilesPreferences {
    fun isWarningAccepted(): Boolean

    fun acceptWarning()
}
