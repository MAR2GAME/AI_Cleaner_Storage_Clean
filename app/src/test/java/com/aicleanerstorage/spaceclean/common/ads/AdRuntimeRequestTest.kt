package com.aicleanerstorage.spaceclean.common.ads

import android.content.Context
import com.aicleanerstorage.spaceclean.app.commercial.AdInterceptor
import com.aicleanerstorage.spaceclean.app.commercial.RuntimeAdDriver
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdRuntimeRequestTest {
    private val driver = RecordingInterstitialDriver()
    private val busy = mutableListOf<Boolean>()
    private val scene = AdScene.ReturnHome(FeatureKey.JUNK_CLEAN)
    private val runtime = runtime()

    @Test
    fun `queued cancellation releases id without affecting current request`() {
        var callbacks = 0
        runtime.run(scene, "first") { callbacks += 1 }
        val queued = runtime.runCancellable(scene, "second") { fail("Cancelled request continued") }
        queued.cancel()
        queued.cancel()
        runtime.runCancellable(scene, "second") { callbacks += 1 }
        assertEquals(1, driver.closed.size)
        driver.closed[0]()
        assertEquals(2, driver.closed.size)
        driver.closed[1]()
        assertEquals(2, callbacks)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `active cancellation advances queue and stale callbacks cannot finish reused id`() {
        var callbacks = 0
        val first = runtime.runCancellable(scene, "same") { fail("Cancelled request continued") }
        runtime.run(scene, "next") { callbacks += 1 }
        first.cancel()
        assertEquals(2, driver.closed.size)
        runtime.runCancellable(scene, "same") { callbacks += 1 }
        first.cancel()
        driver.closed[0]()
        assertEquals(2, driver.closed.size)
        assertEquals(0, driver.preloads)
        driver.closed[1]()
        assertEquals(3, driver.closed.size)
        driver.closed[0]()
        driver.closed[2]()
        driver.closed[2]()
        assertEquals(2, callbacks)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `duplicate cancellable request completes only new caller and never owns original`() {
        var original = 0
        var duplicate = 0
        runtime.run(scene, "same") { original += 1 }
        val handle = runtime.runCancellable(scene, "same") { duplicate += 1 }
        handle.cancel()
        assertEquals(1, duplicate)
        assertEquals(0, original)
        assertEquals(1, driver.closed.size)
        driver.closed.single()()
        assertEquals(1, original)
    }

    @Test
    fun `synchronous repeated close and later cancellation finish once`() {
        driver.show = { close -> close(); close(); true }
        var callbacks = 0
        val handle = runtime.runCancellable(scene, "sync") { callbacks += 1 }
        handle.cancel()
        assertEquals(1, callbacks)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `unavailable activity no ad and show exception release requests`() {
        var callbacks = 0
        for (show in listOf<(() -> Unit) -> Boolean>({ false }, { error("SDK failure") })) {
            driver.show = show
            runtime.runCancellable(scene, "same") { callbacks += 1 }
        }
        driver.available = false
        runtime.runCancellable(scene, "same") { callbacks += 1 }
        assertEquals(3, callbacks)
        assertEquals(listOf(true, false, true, false, true, false), busy)
    }

    @Test
    fun `policy exception after registration releases id and busy state`() {
        var broken = true
        val subject = runtime { if (broken) error("state unavailable") else AdRuntimeState() }
        var callbacks = 0
        subject.runCancellable(scene, "same") { callbacks += 1 }
        broken = false
        driver.show = { close -> close(); true }
        subject.runCancellable(scene, "same") { callbacks += 1 }
        assertEquals(2, callbacks)
        assertEquals(listOf(true, false, true, false), busy)
    }

    @Test
    fun `suspend driver timeout releases active request and late close does not resume twice`() = runTest {
        val subject = AdInterceptor(RuntimeAdDriver(runtime), timeoutMillis = 100L)
        val result = async { subject.intercept(AdNeed.JunkCleanCompletion) }
        runCurrent()
        var next = 0
        runtime.run(scene, "next") { next += 1 }
        advanceTimeBy(100L)
        runCurrent()
        assertEquals(InterceptResult.Continue, result.await())
        assertEquals(2, driver.closed.size)
        driver.closed[0]()
        assertEquals(0, next)
        driver.closed[1]()
        assertEquals(1, next)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `suspend driver cancellation removes queued request before it can show`() = runTest {
        runtime.run(scene, "first") {}
        val waiting = launch { RuntimeAdDriver(runtime).show(InterstitialRequest(scene, "queued")) }
        runCurrent()
        waiting.cancelAndJoin()
        driver.closed.single()()
        assertEquals(1, driver.closed.size)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `completion timeout includes queue wait without cancelling the current ad`() = runTest {
        var original = 0
        runtime.run(scene, "first") { original += 1 }
        val subject = AdInterceptor(RuntimeAdDriver(runtime), timeoutMillis = 100L)
        val waiting = async { subject.intercept(AdNeed.JunkCleanCompletion) }
        runCurrent()
        advanceTimeBy(100L)
        runCurrent()
        assertEquals(InterceptResult.Continue, waiting.await())
        assertEquals(1, driver.closed.size)
        assertEquals(0, original)
        assertEquals(listOf(true), busy)
        driver.closed.single()()
        assertEquals(1, original)
        assertEquals(1, driver.closed.size)
        assertEquals(listOf(true, false), busy)
    }

    @Test
    fun `suspend driver accepts synchronous completion without double resume`() = runTest {
        driver.show = { close -> close(); close(); true }
        RuntimeAdDriver(runtime).show(InterstitialRequest(scene, "sync"))
        driver.closed.single()()
        assertEquals(listOf(true, false), busy)
    }

    private fun runtime(state: () -> AdRuntimeState = { AdRuntimeState() }) = AdRuntime(
        stateProvider = state,
        onInterstitialStateChanged = busy::add,
        driver = driver,
        scheduler = object : AdRuntimeScheduler {
            override fun schedule(delayMillis: Long, block: () -> Unit) = AdRuntimeCancellation {}
        },
        navigationPolicy = AdNavigationPolicy(),
    )
}

private class RecordingInterstitialDriver : AdRuntimeDriver {
    var available = true
    var preloads = 0
    val closed = mutableListOf<() -> Unit>()
    var show: ((() -> Unit) -> Boolean)? = null
    override fun isActivityAvailable() = available
    override fun showInterstitial(areaKey: String, onClosed: () -> Unit): Boolean {
        if (!available) return false
        closed += onClosed
        return show?.invoke(onClosed) ?: true
    }
    override fun preloadAfterInterstitial() { preloads += 1 }
    override fun initConsent(onResult: (Boolean) -> Unit) = false
    override fun showSplashConsent(onFinished: () -> Unit) = Unit
    override fun showOpenAd(areaKey: String, onClosed: () -> Unit, onLoaded: () -> Unit) = Unit
    override fun preloadStartupInterstitial(context: Context) = Unit
    override fun preloadMainPage(context: Context) = Unit
    override fun isAppOpenEnabled() = true
    override fun setAppOpenEnabled(enabled: Boolean) = Unit
    override fun suppressNextAppOpen(suppress: Boolean) = Unit
}
