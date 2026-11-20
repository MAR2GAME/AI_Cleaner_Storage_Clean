package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


enum class FileOperationPhase {
    Scanning,
    Browsing,
    ConfirmDelete,
    Deleting,
    CompleteAnimation,
    Result,
    NoResults,
}
