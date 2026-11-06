package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanSessionStore

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult
import java.util.concurrent.atomic.AtomicReference

object NetworkScanSessionStore {
    private val latestScan = AtomicReference<NetworkScanResult?>(null)

    fun save(scan: NetworkScanResult) {
        latestScan.set(scan)
    }

    fun get(): NetworkScanResult? = latestScan.get()

    fun clear() {
        latestScan.set(null)
    }
}

