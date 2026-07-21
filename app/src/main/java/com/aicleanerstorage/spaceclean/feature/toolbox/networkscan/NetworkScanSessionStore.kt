package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkScanResult
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
