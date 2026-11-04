package com.aicleanerstorage.spaceclean.common.ads

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ColdStartAdsTest {
    @Test fun `consent precedes preload and open with loaded notification and close settling`() = runTest {
        val driver = Driver()
        val states = mutableListOf<Boolean>()
        val job = launch { driver.showColdStart(states::add) }
        runCurrent()
        assertEquals(listOf("enabled:false", "init", "consent", "preload:Startup", "open"), driver.events)
        driver.loaded()
        driver.loaded()
        assertEquals(listOf(true), states)
        advanceTimeBy(7_000)
        runCurrent()
        assertFalse(job.isCompleted)
        driver.closed()
        driver.closed()
        runCurrent()
        advanceTimeBy(799)
        runCurrent()
        assertFalse(job.isCompleted)
        advanceTimeBy(1)
        runCurrent()
        assertTrue(job.isCompleted)
        assertEquals("enabled:true", driver.events.last())
    }
    @Test fun `unloaded ad times out while late sdk notifications cannot change state`() = runTest {
        val driver = Driver()
        val states = mutableListOf<Boolean>()
        val job = launch { driver.showColdStart(states::add) }
        runCurrent()
        advanceTimeBy(6_500)
        runCurrent()
        assertTrue(job.isCompleted)
        val before = states.toList()
        driver.loaded(); driver.closed()
        runCurrent()
        assertEquals(before, states)
        assertTrue(driver.enabled)
    }
    @Test fun `cancel startup restores original app open setting without continuing`() = runTest {
        val driver = Driver().apply { enabled = false }
        var continued = false
        val job = launch { driver.showColdStart(); continued = true }
        runCurrent()
        job.cancelAndJoin()
        driver.loaded(); driver.closed()
        assertFalse(driver.enabled)
        assertFalse(continued)
    }
    @Test fun `external launch and repeated resume retain one return cooldown`() = runTest {
        val driver = Driver()
        val guard = ExternalAppOpenGuard(driver, this)
        guard.onLaunch()
        assertTrue(driver.suppressed)
        guard.onHostResumed()
        advanceTimeBy(600)
        guard.onHostResumed()
        assertTrue(guard.isReturning)
        advanceTimeBy(600)
        runCurrent()
        assertFalse(guard.isReturning)
        assertFalse(driver.suppressed)
        guard.onLaunch()
        guard.onLaunchCancelled()
        assertFalse(driver.suppressed)
        guard.onLaunch()
        guard.onHostResumed()
        guard.dispose()
        assertFalse(guard.isReturning)
        assertFalse(driver.suppressed)
    }
    @Test fun `consent initialization and display each retain bounded waiting`() = runTest {
        val driver = Driver(initReady = false, consentReady = false)
        val job = launch { driver.showColdStart() }
        runCurrent()
        assertEquals(listOf("enabled:false", "init"), driver.events)
        advanceTimeBy(6_500)
        runCurrent()
        assertEquals("consent", driver.events.last())
        advanceTimeBy(6_500)
        runCurrent()
        assertEquals(listOf("preload:Startup", "open"), driver.events.takeLast(2))
        driver.closed()
        runCurrent()
        assertTrue(job.isCompleted)
        assertTrue(driver.enabled)
    }
    private class Driver(val initReady: Boolean = true, val consentReady: Boolean = true) : AdDriver {
        val events = mutableListOf<String>()
        var enabled = true
        var suppressed = false
        var loaded: () -> Unit = {}
        var closed: () -> Unit = {}
        override suspend fun play(areaKey: String) = Unit
        override fun initializeConsent(onReady: () -> Unit): Boolean { events += "init"; if (initReady) onReady(); return initReady }
        override fun showConsent(onClosed: () -> Unit) { events += "consent"; if (consentReady) { onClosed(); onClosed() } }
        override fun preload(stage: AdPreloadStage) { events += "preload:$stage" }
        override fun showOpen(onLoaded: () -> Unit, onClosed: () -> Unit) { events += "open"; loaded = onLoaded; closed = onClosed }
        override fun isAppOpenEnabled() = enabled
        override fun setAppOpenEnabled(enabled: Boolean) { this.enabled = enabled; events += "enabled:$enabled" }
        override fun suppressNextAppOpen(suppress: Boolean) { suppressed = suppress }
    }
}
