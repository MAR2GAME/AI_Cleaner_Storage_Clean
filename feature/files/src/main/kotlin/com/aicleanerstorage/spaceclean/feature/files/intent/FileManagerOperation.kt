package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey

sealed interface FileManagerOperation {
    data class LoadFiles(val feature: FeatureKey) : FileManagerOperation
    data class DeleteFiles(val files: List<ManagedFileItem>) : FileManagerOperation
}
