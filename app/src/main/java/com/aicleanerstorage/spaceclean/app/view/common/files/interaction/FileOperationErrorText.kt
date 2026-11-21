package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText

internal fun FileOperationError.toUiText(): UiText =
    when (this) {
        FileOperationError.ScanFailed -> UiText.Res(R.string.file_scan_failed)
        FileOperationError.DuplicateScanFailed -> UiText.Res(R.string.duplicate_scan_failed)
        FileOperationError.DeletionFailed -> UiText.Res(R.string.deletion_failed)
    }
