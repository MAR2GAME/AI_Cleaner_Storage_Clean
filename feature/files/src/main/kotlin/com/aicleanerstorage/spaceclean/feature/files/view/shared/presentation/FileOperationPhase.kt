package com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


enum class FileOperationPhase {
    Scanning,
    Browsing,
    ConfirmDelete,
    Deleting,
    CompleteAnimation,
    Result,
    NoResults,
}
