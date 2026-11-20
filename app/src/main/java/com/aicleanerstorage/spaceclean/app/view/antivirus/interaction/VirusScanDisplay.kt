package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.model.VirusScanMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TICK_MILLIS = 40L
private const val MAX_DISPLAY_PROGRESS = 0.99f

internal class VirusScanDisplay(
    private val scope: CoroutineScope,
    private val mode: VirusScanMode,
    private val loadAppLabel: suspend (String) -> String,
    private val onUpdate: (VirusScanUpdate) -> Unit,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) {
    private val apps = Channel<String>(Channel.UNLIMITED)
    private val paths = Channel<String>(Channel.UNLIMITED)
    private var displayJob: Job? = null

    fun enqueueApp(packageName: String) {
        apps.trySend(packageName)
    }

    fun enqueuePath(path: String) {
        paths.trySend(path)
    }

    fun start(startedAt: Long) {
        displayJob = scope.launch {
            var appJob: Job? = null
            var appStageEnded = false
            val endOfApps = when (mode) {
                VirusScanMode.Quick -> mode.circleFillThreshold(1)
                VirusScanMode.Deep -> mode.circleStartThreshold(2)
            }
            while (true) {
                val elapsed = nowMillis() - startedAt
                val fraction = (elapsed.toFloat() / mode.minDurationMillis * MAX_DISPLAY_PROGRESS)
                    .coerceIn(0f, MAX_DISPLAY_PROGRESS)
                onUpdate(VirusScanUpdate.ProgressUpdated(fraction))
                if (!appStageEnded && fraction >= endOfApps) {
                    appStageEnded = true
                    appJob?.cancel()
                    apps.cancel()
                    if (mode == VirusScanMode.Deep) {
                        onUpdate(VirusScanUpdate.PathDisplayStarted)
                        launch { displayPaths() }
                    }
                } else if (!appStageEnded && appJob == null && fraction >= mode.circleStartThreshold(1)) {
                    appJob = launch { displayApps() }
                }
                if (fraction >= MAX_DISPLAY_PROGRESS) break
                delay(TICK_MILLIS)
            }
        }
    }

    private suspend fun displayApps() {
        val seen = mutableSetOf<String>()
        var lastUpdate: Long? = null
        for (name in apps) {
            if (!seen.add(name)) continue
            val label = loadAppLabel(name)
            waitForNextItem(lastUpdate)
            onUpdate(VirusScanUpdate.AppDisplayed(label, name))
            lastUpdate = nowMillis()
        }
    }

    private suspend fun displayPaths() {
        var lastUpdate: Long? = null
        for (path in paths) {
            waitForNextItem(lastUpdate)
            onUpdate(VirusScanUpdate.PathDisplayed(path))
            lastUpdate = nowMillis()
        }
    }

    private suspend fun waitForNextItem(lastUpdate: Long?) {
        if (lastUpdate != null) delay(mode.displayUpdateIntervalMillis - (nowMillis() - lastUpdate))
    }

    fun stop() {
        displayJob?.cancel()
        apps.cancel()
        paths.cancel()
    }
}
