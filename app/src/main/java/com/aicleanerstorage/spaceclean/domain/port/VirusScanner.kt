package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.VirusScanItemModel

import com.aicleanerstorage.spaceclean.domain.model.VirusScanErrorKind

import com.aicleanerstorage.spaceclean.domain.model.VirusScanMode

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
