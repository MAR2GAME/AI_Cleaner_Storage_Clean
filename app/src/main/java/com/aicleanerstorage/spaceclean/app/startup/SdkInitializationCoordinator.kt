package com.aicleanerstorage.spaceclean.app.startup

import com.aicleanerstorage.spaceclean.common.startup.SdkInitialization
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class SdkInitializationCoordinator(
    private val scope: CoroutineScope,
    private val advertiseInitializer: suspend () -> Unit,
    private val analyticsInitializer: suspend () -> Unit,
    private val notificationDefaultsInitializer: suspend () -> Unit,
    private val logger: (String, Throwable?) -> Unit = { _, _ -> },
) : SdkInitialization {
    private val started = AtomicBoolean(false)
    private val advertiseFinished = CompletableDeferred<Unit>()
    private val _state = MutableStateFlow(AppSdkInitializationState())
    val state: StateFlow<AppSdkInitializationState> = _state.asStateFlow()

    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch {
            runComponent(SdkComponent.ADVERTISE, advertiseInitializer)
            advertiseFinished.complete(Unit)
        }
        scope.launch {
            withTimeoutOrNull(6_500L) { advertiseFinished.await() }
            runComponent(SdkComponent.ANALYTICS, analyticsInitializer)
        }
        scope.launch {
            advertiseFinished.await()
            runComponent(SdkComponent.NOTIFICATION_DEFAULTS, notificationDefaultsInitializer)
        }
    }

    override suspend fun awaitAdvertiseReady(timeoutMillis: Long): Boolean =
        awaitReady(timeoutMillis) { state ->
            when (state.advertise.status) {
                InitializationStatus.SUCCEEDED -> Readiness.READY
                InitializationStatus.FAILED -> Readiness.FAILED
                else -> Readiness.WAITING
            }
        }

    private suspend fun awaitReady(
        timeoutMillis: Long,
        evaluate: (AppSdkInitializationState) -> Readiness,
    ): Boolean {
        when (evaluate(state.value)) {
            Readiness.READY -> return true
            Readiness.FAILED -> return false
            Readiness.WAITING -> Unit
        }
        if (timeoutMillis <= 0L) return false
        return try {
            withTimeoutOrNull(timeoutMillis) {
                state.map(evaluate).first { it != Readiness.WAITING } == Readiness.READY
            } ?: false
        } catch (error: CancellationException) {
            throw error
        }
    }

    private suspend fun runComponent(component: SdkComponent, initializer: suspend () -> Unit) {
        update(component, ComponentInitializationState(InitializationStatus.RUNNING))
        try {
            initializer()
            update(component, ComponentInitializationState(InitializationStatus.SUCCEEDED))
            logger("${component.logName} initialization succeeded", null)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            update(component, ComponentInitializationState(InitializationStatus.FAILED, error))
            logger("${component.logName} initialization failed", error)
        }
    }

    private fun update(component: SdkComponent, value: ComponentInitializationState) {
        _state.update { current ->
            when (component) {
                SdkComponent.ADVERTISE -> current.copy(advertise = value)
                SdkComponent.ANALYTICS -> current.copy(analytics = value)
                SdkComponent.NOTIFICATION_DEFAULTS -> current.copy(notificationDefaults = value)
            }
        }
    }

    private enum class SdkComponent(val logName: String) {
        ADVERTISE("AdvertiseSdk"),
        ANALYTICS("Analytics"),
        NOTIFICATION_DEFAULTS("Advertise notification defaults"),
    }

    private enum class Readiness { WAITING, READY, FAILED }
}

enum class InitializationStatus { NOT_STARTED, RUNNING, SUCCEEDED, FAILED }

data class ComponentInitializationState(
    val status: InitializationStatus = InitializationStatus.NOT_STARTED,
    val error: Throwable? = null,
)

data class AppSdkInitializationState(
    val advertise: ComponentInitializationState = ComponentInitializationState(),
    val analytics: ComponentInitializationState = ComponentInitializationState(),
    val notificationDefaults: ComponentInitializationState = ComponentInitializationState(),
)
