package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.domain.model.NetworkScanResultModel
import java.util.concurrent.atomic.AtomicReference

object NetworkScanSessionStore {
    private val latestScan = AtomicReference<NetworkScanResultModel?>(null)

    fun save(scan: NetworkScanResultModel) {
        latestScan.set(scan)
    }

    fun get(): NetworkScanResultModel? = latestScan.get()

    fun clear() {
        latestScan.set(null)
    }
}

