package com.aicleanerstorage.spaceclean.core.contracts.ads

fun interface AdDriver {
    suspend fun play(areaKey: String)
    fun isAvailable(): Boolean = true
    fun preload(stage: AdPreloadStage) = Unit
    fun initializeConsent(onReady: () -> Unit): Boolean = false
    fun showConsent(onClosed: () -> Unit) = onClosed()
    fun showOpen(onLoaded: () -> Unit, onClosed: () -> Unit) = onClosed()
    fun isAppOpenEnabled(): Boolean = true
    fun setAppOpenEnabled(enabled: Boolean) = Unit
    fun suppressNextAppOpen(suppress: Boolean) = Unit
}

enum class AdPreloadStage { Startup, MainPage, InterstitialClosed }

