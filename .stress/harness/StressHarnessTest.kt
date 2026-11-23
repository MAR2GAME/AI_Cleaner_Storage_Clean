package com.aicleanerstorage.spaceclean.stress

import androidx.lifecycle.ViewModelStore
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkOperation
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceCpuInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceSensorInfoModel
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCandidateModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkSource
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import com.aicleanerstorage.spaceclean.domain.rule.JunkClassificationRule
import com.aicleanerstorage.spaceclean.domain.rule.JunkScanRule
import com.aicleanerstorage.spaceclean.domain.rule.SimilarPhotoRule
import com.aicleanerstorage.spaceclean.testsupport.InMemoryAppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.asCoroutineDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * 涓存椂鍘嬫祴鑴氭湰锛氫笉璧?CI锛屽彧鍦ㄦ湰鏈虹敤 `--tests "*StressHarnessTest*"` 瑙﹀彂銆? * 缁撴灉杩藉姞鍐欏叆 app/build/stress-metrics.txt銆? */
class StressHarnessTest {
    private val metricsFile = File("build/stress-metrics.txt")

    @Before
    fun setUp() {
        metricsFile.parentFile?.mkdirs()
        if (!metricsFile.exists()) {
            metricsFile.writeText(
                "jvm=${System.getProperty("java.version")} " +
                    "cores=${Runtime.getRuntime().availableProcessors()} " +
                    "maxHeapMb=${Runtime.getRuntime().maxMemory() / MB}\n",
            )
        }
        Dispatchers.setMain(Dispatchers.Default)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test(timeout = 120_000)
    fun duplicateGroupingThroughput() {
        val count = 60_000
        val buckets = 12_000
        val hashes = HashMap<Int, String>(count)
        val candidates = ArrayList<DuplicateFileRule.Candidate<Int>>(count)
        for (index in 0 until count) {
            val bucket = index % buckets
            candidates +=
                DuplicateFileRule.Candidate(
                    value = index,
                    identity = "id-$index",
                    name = "photo_$index.jpg",
                    sizeBytes = bucket.toLong() * 4096L + 1L,
                    modifiedMillis = index.toLong(),
                )
            hashes[index] = "hash-$bucket"
        }

        val elapsed = measure("duplicate.group input=$count groups=$buckets") {
            val groups = DuplicateFileRule.group(candidates) { hashes[it] }
            assertEquals(buckets, groups.size)
            assertEquals(count, groups.sumOf { it.size })
        }
        metric("duplicate.group throughputFilesPerSec=${count * 1000L / (elapsed + 1)}")
    }

    @Test(timeout = 120_000)
    fun duplicateWorstCaseSingleSize() {
        val count = 40_000
        val candidates =
            (0 until count).map { index ->
                DuplicateFileRule.Candidate(
                    value = index,
                    identity = "id-$index",
                    name = "same_$index.bin",
                    sizeBytes = 8L * 1024L * 1024L,
                    modifiedMillis = index.toLong(),
                )
            }
        measure("duplicate.group worst-case same-size input=$count groups=${count / 4}") {
            val groups = DuplicateFileRule.group(candidates) { "h-${it / 4}" }
            assertEquals(count / 4, groups.size)
            assertEquals(count, groups.sumOf { it.size })
        }
    }

    @Test(timeout = 120_000)
    fun duplicateVerificationThroughput() {
        val retained = (0 until 20).toList()
        repeat(3) { round ->
            measure("duplicate.hasVerifiedCopy round=$round calls=10000 retained=20") {
                for (index in 0 until 10_000) {
                    val verified =
                        DuplicateFileRule.hasVerifiedCopy(
                            candidate = 1_000 + index,
                            retainedCandidates = retained,
                            expectedHash = "h-${index % 20}",
                            identity = { "id-$it" },
                            contentHash = { "h-${it % 20}" },
                        )
                    assertTrue(verified)
                }
            }
        }
    }

    @Test(timeout = 120_000)
    fun similarPhotoGroupingThroughput() {
        val count = 100_000
        val files =
            (0 until count).map { index ->
                ManagedFileModel(
                    id = index.toLong(),
                    uri = "content://media/external/images/media/$index",
                    path = "/storage/emulated/0/DCIM/Camera/IMG_$index.jpg",
                    name = "IMG_$index.jpg",
                    sizeBytes = (index % 500).toLong() * 1024L * 1024L + index,
                    modifiedSeconds = 1_700_000_000L + index * 60L,
                    mimeType = "image/jpeg",
                    bucketName = "Camera${index % 4}",
                    kind = FileKind.Image,
                )
            }

        measure("similarPhoto.group input=$count full-limit") {
            val groups = SimilarPhotoRule.group(files, limit = files.size)
            assertTrue(groups.isNotEmpty())
        }
        measure("similarPhoto.group input=$count default-limit=60") {
            SimilarPhotoRule.group(files)
        }
    }

    @Test(timeout = 120_000)
    fun junkNormalizeThroughput() {
        val count = 200_000
        val files =
            (0 until count).map { index ->
                val half = count / 2
                val path = if (index < half) "/storage/emulated/0/cache/a$index.tmp" else "/storage/emulated/0/cache/a${index - half}.tmp"
                JunkFileModel(
                    id = "id-$index",
                    path = path,
                    name = "a$index.tmp",
                    sizeBytes = (index % 4096).toLong() * 1024L,
                    category = if (index % 2 == 0) JunkCategory.DUPLICATE else JunkCategory.CACHE,
                    modifiedMillis = index.toLong(),
                )
            }

        measure("junk.normalize input=$count expected-unique=${count / 2}") {
            val normalized = JunkScanRule.normalize(files)
            assertEquals(count / 2, normalized.size)
            assertEquals(normalized.size, normalized.map { it.path }.distinct().size)
            assertEquals(JunkCategory.DUPLICATE, normalized.first().category)
        }
    }

    @Test(timeout = 120_000)
    fun classificationThroughput() {
        val names =
            listOf(
                "VID_20260101.mp4",
                "IMG_0001.HEIC",
                "report.pdf",
                "backup.zip",
                "song.mp3",
                "notes.txt",
                "archive.7z",
                "weird.unknown",
                ".hidden.tmp",
                "photo.JPG",
            )
        val mimes = listOf(null, "image/jpeg", "video/mp4", "application/pdf", "text/plain", "application/zip")
        val paths = listOf(null, "/sdcard/DCIM/a.jpg", "/system/app/x.apk", "/sdcard/Download/a.apk", "/data/local/tmp/a.tmp")
        val operations = 300_000

        measure("file.classification ops=$operations calls=${operations * 5}") {
            var hits = 0
            for (index in 0 until operations) {
                val name = names[index % names.size]
                val mime = mimes[index % mimes.size]
                if (FileClassificationRule.isMediaFile(name, mime)) hits++
                if (FileClassificationRule.isDocumentFile(name, mime)) hits++
                FileClassificationRule.typeForName(name)
                FileClassificationRule.shouldSkipPath(paths[index % paths.size], name)
                FileClassificationRule.identity(paths[index % paths.size], "content://x/$index")
                hits++
            }
            assertTrue(hits > 0)
        }

        val now = 1_800_000_000_000L
        val candidates =
            (0 until 20_000).map { index ->
                JunkCandidateModel(
                    path = if (index % 3 == 0) "/storage/emulated/0/Download/old_$index.apk" else "/storage/emulated/0/cache/f$index.tmp",
                    name = if (index % 3 == 0) "old (1).apk" else "f$index.tmp",
                    sizeBytes = (index % 5000).toLong() * 1024L,
                    modifiedMillis = now - (index % 90).toLong() * 24L * 3600L * 1000L,
                    source = if (index % 7 == 0) JunkSource.AppCache else JunkSource.PublicStorage,
                )
            }
        measure("junk.classification candidates=${candidates.size}") {
            val classified = candidates.count { JunkClassificationRule.categoryOf(it, now) != null }
            assertTrue(classified > 0)
        }
    }

    @Test(timeout = 180_000)
    fun rulesConcurrentDeterminism() {
        val count = 5_000
        val candidates =
            (0 until count).map { index ->
                DuplicateFileRule.Candidate(
                    value = index,
                    identity = "id-$index",
                    name = "f$index",
                    sizeBytes = (index % 1_000).toLong() * 1024L,
                    modifiedMillis = index.toLong(),
                )
            }
        val expected = DuplicateFileRule.group(candidates) { "h-${it % 1_000}" }.size
        val pool = Executors.newFixedThreadPool(8)
        val failures = ConcurrentLinkedQueue<Throwable>()
        val seenCounts = ConcurrentHashMap<Int, Int>()
        val start = CountDownLatch(1)
        val done = CountDownLatch(8)
        val rounds = 20
        val wallStart = System.nanoTime()
        repeat(8) {
            pool.execute {
                start.await()
                try {
                    repeat(rounds) {
                        val groups = DuplicateFileRule.group(candidates) { "h-${it % 1_000}" }
                        seenCounts.merge(groups.size, 1, Int::plus)
                    }
                } catch (error: Throwable) {
                    failures += error
                } finally {
                    done.countDown()
                }
            }
        }
        start.countDown()
        assertTrue("concurrent group timed out", done.await(120, java.util.concurrent.TimeUnit.SECONDS))
        pool.shutdown()
        val wallMs = (System.nanoTime() - wallStart) / 1_000_000
        assertEquals(emptyList<Throwable>(), failures.toList())
        assertEquals(setOf(expected), seenCounts.keys)
        assertEquals(8 * rounds, seenCounts.values.sum())
        metric("rules.concurrent threads=8 rounds=$rounds perRound=$count wallMs=$wallMs consistentGroups=$expected")
    }

    @Test(timeout = 180_000)
    fun junkCleanLifecycleStorm() {
        val repo = InstantJunkRepository(files = 40)
        val heapBefore = heapUsedMb()
        val scanCycles = 40
        val fullCycles = 8
        val scanStart = System.nanoTime()

        repeat(scanCycles) {
            val store = ViewModelStore()
            val viewModel = newJunkViewModel(repo, store)
            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            awaitPhase(viewModel) { it == JunkCleanPhase.Preview }
            viewModel.dispatch(JunkCleanAction.ItemToggled("junk-0"))
            viewModel.dispatch(JunkCleanAction.CategoriesToggled(listOf(JunkCategory.CACHE)))
            viewModel.dispatch(JunkCleanAction.BackClicked)
            store.clear()
        }
        val scanMs = (System.nanoTime() - scanStart) / 1_000_000
        metric("junkClean.scanCycles count=$scanCycles wallMs=$scanMs")

        val cleanStart = System.nanoTime()
        repeat(fullCycles) {
            val store = ViewModelStore()
            val viewModel = newJunkViewModel(repo, store)
            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            awaitPhase(viewModel) { it == JunkCleanPhase.Preview }
            viewModel.dispatch(JunkCleanAction.CleanClicked)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Clean, granted = true))
            awaitPhase(viewModel, timeoutMs = 10_000) { it == JunkCleanPhase.Complete }
            assertEquals(40, viewModel.state.value.cleanResult.cleanedCount)
            store.clear()
        }
        val cleanMs = (System.nanoTime() - cleanStart) / 1_000_000
        metric("junkClean.fullCycles count=$fullCycles wallMs=$cleanMs")
        val heapAfter = heapUsedMb()
        metric("junkClean.heapDeltaMb before=$heapBefore after=$heapAfter delta=${heapAfter - heapBefore}")
        assertTrue("heap grew by ${heapAfter - heapBefore}MB", heapAfter - heapBefore < 128L)
    }

    @Test(timeout = 180_000)
    fun junkCleanConcurrentActionStorm() {
        val repo = InstantJunkRepository(files = 100, scanDelayMs = 1)
        val store = ViewModelStore()
        val viewModel = newJunkViewModel(repo, store)
        val pool = Executors.newFixedThreadPool(8)
        val failures = ConcurrentLinkedQueue<Throwable>()
        val done = CountDownLatch(8)
        val actions =
            listOf(
                JunkCleanAction.Entered,
                JunkCleanAction.ScanRequested,
                JunkCleanAction.CleanClicked,
                JunkCleanAction.StopConfirmed,
                JunkCleanAction.StopDismissed,
                JunkCleanAction.ContinueClicked,
                JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true),
                JunkCleanAction.PermissionResultReceived(JunkOperation.Clean, granted = true),
                JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = false),
            )
        val dispatches = AtomicInteger()
        val deadline = System.currentTimeMillis() + 3_000
        repeat(8) { threadIndex ->
            pool.execute {
                try {
                    var cursor = threadIndex
                    while (System.currentTimeMillis() < deadline) {
                        viewModel.dispatch(actions[cursor % actions.size])
                        dispatches.incrementAndGet()
                        cursor++
                    }
                } catch (error: Throwable) {
                    failures += error
                } finally {
                    done.countDown()
                }
            }
        }
        assertTrue("storm timed out", done.await(30, java.util.concurrent.TimeUnit.SECONDS))
        pool.shutdown()
        metric("junkClean.concurrentStorm dispatches=${dispatches.get()} failures=${failures.size} phase=${viewModel.state.value.phase}")
        assertEquals(emptyList<Throwable>(), failures.toList())
        store.clear()
    }

    @Test(timeout = 180_000)
    fun homeRefreshStorm() {
        val availability = AtomicInteger()
        val refreshes = AtomicInteger()
        val maxConcurrency = AtomicInteger()
        val deviceRepository = deviceInfoProxy(availability, refreshes, maxConcurrency)
        val appLock = appLockProxy()
        val pool = Executors.newFixedThreadPool(8)
        val ioDispatcher = pool.asCoroutineDispatcher()
        val store = ViewModelStore()
        val viewModel =
            HomeViewModel(
                repository = deviceRepository,
                appLockRepository = appLock,
                settingsRepository = InMemoryAppSettingsRepository(),
                ioDispatcher = ioDispatcher,
                autoRateDispatcher = Dispatchers.Default,
                autoRatePromptDelayMillis = 60_000L,
            )
        store.put("home", viewModel)

        val requests = 500
        val start = System.nanoTime()
        repeat(requests) { viewModel.dispatch(HomeAction.SummaryRefreshRequested) }
        val deadline = System.currentTimeMillis() + 30_000
        var last = -1
        while (System.currentTimeMillis() < deadline) {
            val current = refreshes.get()
            if (current == last && current >= 1) break
            last = current
            Thread.sleep(150)
        }
        Thread.sleep(200)
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        metric(
            "home.refreshStorm requests=$requests completed=${refreshes.get()} " +
                "maxConcurrency=${maxConcurrency.get()} wallMs=$elapsedMs",
        )
        assertTrue("no refresh completed", refreshes.get() >= 1)
        assertTrue("refresh storm not deduplicated: ${refreshes.get()}", refreshes.get() <= 2)
        assertEquals(false, viewModel.state.value.summary.isLoading)
        store.clear()
        ioDispatcher.close()
        pool.shutdown()
    }

    @Test(timeout = 120_000)
    fun homeEventFlood() {
        val store = ViewModelStore()
        val viewModel =
            HomeViewModel(
                repository = deviceInfoProxy(AtomicInteger(), AtomicInteger(), AtomicInteger()),
                appLockRepository = appLockProxy(),
                settingsRepository = InMemoryAppSettingsRepository(),
                ioDispatcher = Dispatchers.Default,
                autoRateDispatcher = Dispatchers.Default,
                autoRatePromptDelayMillis = 60_000L,
            )
        store.put("home", viewModel)
        val events = 20_000
        val start = System.nanoTime()
        repeat(events) {
            viewModel.dispatch(HomeAction.NavigationRequested(com.aicleanerstorage.spaceclean.base.navigation.AppDestination.Settings))
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        metric("home.eventFlood events=$events wallMs=$elapsedMs")
        assertTrue("event flood too slow: ${elapsedMs}ms", elapsedMs < 5_000)
        store.clear()
    }

    @Test(timeout = 180_000)
    fun viewModelAllocationSoak() {
        val repo = InstantJunkRepository(files = 10)
        val heapBefore = heapUsedMb()
        val count = 300
        repeat(count) {
            val store = ViewModelStore()
            val viewModel = newJunkViewModel(repo, store)
            viewModel.dispatch(JunkCleanAction.Entered)
            store.clear()
        }
        val heapAfter = heapUsedMb()
        metric("viewModel.soak created=$count cleared=$count heapBeforeMb=$heapBefore heapAfterMb=$heapAfter")
        assertTrue("heap grew by ${heapAfter - heapBefore}MB", heapAfter - heapBefore < 96L)
    }

    // --- helpers ---

    private fun newJunkViewModel(
        repo: JunkCleanRepository,
        store: ViewModelStore,
    ): JunkCleanViewModel {
        val viewModel =
            JunkCleanViewModel(
                cleanRepository = repo,
                ioDispatcher = Dispatchers.Default,
                scanPreviewMinMillis = 0L,
                cleaningAnimationMinMillis = 0L,
            )
        store.put("junk-${System.identityHashCode(viewModel)}", viewModel)
        return viewModel
    }

    private fun awaitPhase(
        viewModel: JunkCleanViewModel,
        timeoutMs: Long = 5_000,
        predicate: (JunkCleanPhase) -> Boolean,
    ) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (predicate(viewModel.state.value.phase)) return
            Thread.sleep(2)
        }
        throw AssertionError("phase timeout, actual=${viewModel.state.value.phase}")
    }

    private fun deviceInfoProxy(
        availability: AtomicInteger,
        completed: AtomicInteger,
        maxConcurrency: AtomicInteger,
    ): DeviceInfoRepository =
        proxy(DeviceInfoRepository::class.java) { method ->
            when (method.name) {
                "hardwareInfo" ->
                    DeviceHardwareInfoModel(
                        model = "stress-fixture",
                        androidVersion = "16",
                        screenSize = "1260x2800",
                        screenDensity = "560",
                        multiTouchSupported = true,
                        sensors = DeviceSensorInfoModel(false, false, false, false, false, false, false),
                        cpu = DeviceCpuInfoModel(hardware = "fixture", model = "fixture", cores = 8, maxFrequency = "3.2GHz"),
                    )
                "internalStorageInfo" -> CapacityInfoModel(totalBytes = 461_000_000_000L, availableBytes = 149_000_000_000L)
                "batteryInfo" -> {
                    val inFlight = availability.incrementAndGet()
                    maxConcurrency.accumulateAndGet(inFlight, ::maxOf)
                    try {
                        Thread.sleep(1)
                        completed.incrementAndGet()
                        BatteryInfoModel(levelPercent = 100)
                    } finally {
                        availability.decrementAndGet()
                    }
                }
                "memoryInfo" -> CapacityInfoModel(totalBytes = 12_000_000_000L, availableBytes = 6_000_000_000L)
                else -> defaultValue(method.returnType)
            }
        }

    private fun appLockProxy(): AppLockRepository =
        proxy(AppLockRepository::class.java) { method ->
            when (method.name) {
                "observeLockedAppCount" -> flowOf(0)
                "currentLockedAppCount" -> 0
                "lockedPackages" -> emptySet<String>()
                "lockableApps" -> emptyList<Any>()
                else -> defaultValue(method.returnType)
            }
        }

    private fun <T> proxy(
        type: Class<T>,
        handler: (java.lang.reflect.Method) -> Any?,
    ): T =
        Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ ->
            when (method.name) {
                "toString" -> "stress-proxy(${type.simpleName})"
                "hashCode" -> System.identityHashCode(type)
                "equals" -> false
                else -> handler(method)
            }
        } as T

    private fun defaultValue(type: Class<*>): Any? =
        when (type) {
            Boolean::class.javaPrimitiveType -> false
            Int::class.javaPrimitiveType -> 0
            Long::class.javaPrimitiveType -> 0L
            Float::class.javaPrimitiveType -> 0f
            Double::class.javaPrimitiveType -> 0.0
            else -> null
        }

    private fun measure(
        label: String,
        block: () -> Unit,
    ): Long {
        val start = System.nanoTime()
        block()
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        metric("$label elapsedMs=$elapsedMs")
        return elapsedMs
    }

    private fun metric(line: String) {
        synchronized(metricsFile) {
            metricsFile.appendText("$line\n")
        }
        println("[STRESS] $line")
    }

    private fun heapUsedMb(): Long {
        repeat(3) { System.gc(); Thread.sleep(80) }
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / MB
    }

    private class InstantJunkRepository(
        private val files: Int,
        private val scanDelayMs: Long = 0L,
    ) : JunkCleanRepository {
        private val result =
            JunkScanResultModel(
                (0 until files).map { index ->
                    JunkFileModel(
                        id = "junk-$index",
                        path = "/storage/emulated/0/cache/stress-$index.tmp",
                        name = "stress-$index.tmp",
                        sizeBytes = 1024L * (index + 1),
                        category = JunkCategory.CACHE,
                        modifiedMillis = Random.nextLong(1_600_000_000_000L, 1_800_000_000_000L),
                    )
                },
            )

        override suspend fun scan(onProgress: (JunkScanProgressModel) -> Unit): Result<JunkScanResultModel> {
            if (scanDelayMs > 0) Thread.sleep(scanDelayMs)
            onProgress(JunkScanProgressModel.IDLE)
            return Result.success(result)
        }

        override suspend fun cleanMemory(): Result<JunkMemoryCleanModel> = Result.success(JunkMemoryCleanModel(freedBytes = 4096L))

        override suspend fun deleteFiles(
            files: List<JunkFileModel>,
            shouldStop: () -> Boolean,
        ): Result<List<JunkDeleteOutcomeModel>> =
            Result.success(files.map { JunkDeleteOutcomeModel(it, DeleteStatus.Deleted) })
    }

    private companion object {
        const val MB = 1024L * 1024L
    }
}

