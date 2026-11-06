package com.aicleanerstorage.spaceclean.core.contracts.startup

interface SdkInitialization {
    suspend fun awaitAdvertiseReady(timeoutMillis: Long = 6_500L): Boolean
}
