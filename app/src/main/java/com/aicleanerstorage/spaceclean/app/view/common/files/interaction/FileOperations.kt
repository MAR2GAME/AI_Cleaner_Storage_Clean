package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class FileOperations<T>(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val load: suspend () -> T,
) {
    private var activeJob: Job? = null

    fun cancel() {
        activeJob?.cancel()
        activeJob = null
    }

    /**
     * 加载列表并保证扫描动画至少展示 [delayMillis]。
     *
     * 扫描耗时超过 [delayMillis] 时不再额外等待，避免“扫描完还要空转”造成体感卡顿。
     */
    fun scan(
        delayMillis: Long,
        onStart: () -> Unit,
        onLoaded: (T) -> Unit,
        onFailure: (Exception) -> Unit,
    ) = start(onStart, onFailure) {
        val startedAtNanos = System.nanoTime()
        val items = withContext(ioDispatcher) { load() }
        val elapsedMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L
        delay((delayMillis - elapsedMillis).coerceAtLeast(0L))
        currentCoroutineContext().ensureActive()
        onLoaded(items)
    }

    fun <R> changeFiles(
        hasSelection: Boolean,
        onEmptySelection: () -> Unit,
        onStart: () -> Unit,
        operationDelayMillis: Long,
        completeDelayMillis: Long,
        operation: suspend () -> R,
        shouldShowResult: (R) -> Boolean = { true },
        onReloaded: (R, T) -> Unit,
        onResult: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (activeJob?.isCompleted == false) return
        if (!hasSelection) {
            onEmptySelection()
            return
        }
        start(onStart, onFailure) {
            val result = withContext(ioDispatcher) { operation() }
            currentCoroutineContext().ensureActive()
            val showResult = shouldShowResult(result)
            if (showResult) delay(operationDelayMillis)
            val items = withContext(ioDispatcher) { load() }
            currentCoroutineContext().ensureActive()
            onReloaded(result, items)
            if (!showResult) return@start
            delay(completeDelayMillis)
            onResult()
        }
    }

    private fun start(
        onStart: () -> Unit,
        onFailure: (Exception) -> Unit,
        block: suspend () -> Unit,
    ) {
        if (activeJob?.isCompleted == false) return
        val job =
            scope.launch(start = CoroutineStart.LAZY) {
                try {
                    block()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    onFailure(error)
                }
            }
        activeJob = job
        onStart()
        job.start()
    }
}
