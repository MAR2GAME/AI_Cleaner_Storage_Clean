package com.aicleanerstorage.spaceclean.common.startup

interface SdkInitialization {
    suspend fun awaitAdvertiseReady(timeoutMillis: Long = 6_500L): Boolean
}
