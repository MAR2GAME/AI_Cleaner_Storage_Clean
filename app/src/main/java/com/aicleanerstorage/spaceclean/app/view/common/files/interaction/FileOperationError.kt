package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

/** Failures shared by the file-manager family (list, media, duplicates, privacy). */
sealed interface FileOperationError {
    data object ScanFailed : FileOperationError

    data object DuplicateScanFailed : FileOperationError

    data object DeletionFailed : FileOperationError
}
