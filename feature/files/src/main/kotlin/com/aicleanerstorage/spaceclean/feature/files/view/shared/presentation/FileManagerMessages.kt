package com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.appString
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.fileScanFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.duplicateScanFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.deletionFailedMessage


import com.aicleanerstorage.spaceclean.core.ui.R

const val FILE_DELETE_ANIMATION_MIN_MILLIS = 2000L

fun appString(resId: Int): String = fallbackString(resId)

fun fileScanFailedMessage(): String =
    runCatching { appString(R.string.file_scan_failed) }.getOrDefault("File scan failed.")

fun duplicateScanFailedMessage(): String =
    runCatching { appString(R.string.duplicate_scan_failed) }.getOrDefault("Duplicate file scan failed.")

fun deletionFailedMessage(): String =
    runCatching { appString(R.string.deletion_failed) }.getOrDefault("Deletion failed.")

private fun fallbackString(resId: Int): String =
    when (resId) {
        R.string.file_scan_failed -> "File scan failed."
        R.string.duplicate_scan_failed -> "Duplicate file scan failed."
        R.string.deletion_failed -> "Deletion failed."
        else -> ""
    }
