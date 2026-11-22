package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.error.VirusScanError
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode

interface VirusScanner {
    fun startScan(
        mode: VirusScanMode,
        listener: Listener,
    )

    fun cancelScan()

    interface Listener {
        fun onScanStarted()

        fun onScanProgress(item: VirusScanItemModel?)

        fun onScanError(error: VirusScanError)

        fun onScanCanceled()

        fun onScanInterrupt()

        fun onScanFinished()
    }
}
