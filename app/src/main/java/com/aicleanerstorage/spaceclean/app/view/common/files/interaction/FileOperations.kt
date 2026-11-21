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

    fun scan(
        delayMillis: Long,
        onStart: () -> Unit,
        onLoaded: (T) -> Unit,
        onFailure: (Exception) -> Unit,
    ) = start(onStart, onFailure) {
        val items = withContext(ioDispatcher) { load() }
        delay(delayMillis)
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
        isSuccessful: (R) -> Boolean,
        onCompleteAnimation: (R, T) -> Unit,
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
            check(isSuccessful(result)) { "Deletion failed" }
            delay(operationDelayMillis)
            val items = withContext(ioDispatcher) { load() }
            currentCoroutineContext().ensureActive()
            onCompleteAnimation(result, items)
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
