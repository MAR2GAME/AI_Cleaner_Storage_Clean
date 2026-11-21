package com.aicleanerstorage.spaceclean.data.security

import com.aicleanerstorage.spaceclean.data.security.model.VirusScanErrorKind
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanItemModel
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode

interface VirusScanner {
    fun startScan(
        mode: VirusScanMode,
        listener: Listener,
    )

    fun cancelScan()

    interface Listener {
        fun onScanStarted()

        fun onScanProgress(item: VirusScanItemModel?)

        fun onScanError(
            code: Int,
            message: String?,
            kind: VirusScanErrorKind,
        )

        fun onScanCanceled()

        fun onScanInterrupt()

        fun onScanFinished()
    }
}
