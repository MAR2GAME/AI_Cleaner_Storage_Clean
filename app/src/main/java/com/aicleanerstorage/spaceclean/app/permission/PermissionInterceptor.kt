package com.aicleanerstorage.spaceclean.app.permission

import android.content.Context
import com.aicleanerstorage.spaceclean.common.permission.*
import com.aicleanerstorage.spaceclean.core.presentation.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PermissionInterceptor internal constructor(
    private val checkGranted: (PermissionType) -> Boolean,
    private val recordRequested: (PermissionType) -> Unit = {},
    private val recordDenied: (PermissionType) -> Unit = {},
) : IntentInterceptor, PermissionGateway {
    constructor(context: Context, preferences: PermissionPreferences) : this(
        checkGranted = { permissionGranted(context.applicationContext, it) },
        recordRequested = {
            when (it) {
                PermissionType.Location -> preferences.saveLocationRuntimePermissionRequestedBefore()
                PermissionType.PostNotifications -> preferences.saveNotificationRuntimePermissionRequestedBefore()
                else -> Unit
            }
        },
        recordDenied = { if (it == PermissionType.Location) preferences.saveLocationRuntimePermissionDenied() },
    )

    internal enum class Stage { AwaitingUser, LaunchRequested, WaitingRuntime, WaitingSettings }
    internal class Request(
        val target: PermissionTarget,
        val settingsOnly: Boolean,
        missing: PermissionType,
        stage: Stage,
    ) {
        internal val result = CompletableDeferred<PermissionResult>()
        internal var abandoned = false
        internal var observedPause = false
        internal var runtimeLaunched = false
        internal var explanationAccepted = false
        internal var stage = stage
        internal var missing = missing
    }
    internal data class Prompt(val owner: Request, val stage: Stage, val missing: PermissionType)
    private val mutableRequest = MutableStateFlow<Prompt?>(null)
    internal val request = mutableRequest.asStateFlow()

    override fun isGranted(permission: PermissionType): Boolean = try {
        checkGranted(permission)
    } catch (error: CancellationException) { throw error } catch (_: Exception) { false }

    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy !is PermissionNeed) return InterceptResult.NotHandled
        return when (ensure(policy)) {
            PermissionResult.Granted -> InterceptResult.Continue
            PermissionResult.Denied -> InterceptResult.Block
            PermissionResult.Busy -> InterceptResult.Busy
        }
    }

    override suspend fun ensure(target: PermissionTarget, mode: PermissionPromptMode): PermissionResult =
        await(target, mode, settingsOnly = false)

    override suspend fun openSettings(permission: PermissionType): PermissionResult =
        await(PermissionTarget.Permission(permission), PermissionPromptMode.Direct, settingsOnly = true)

    private suspend fun await(target: PermissionTarget, mode: PermissionPromptMode, settingsOnly: Boolean): PermissionResult =
        withContext(Dispatchers.Main.immediate) {
            currentCoroutineContext().ensureActive()
            if (mutableRequest.value != null) return@withContext PermissionResult.Busy
            val missing = target.requiredPermissions.firstOrNull { !isGranted(it) }
            if (missing == null && !settingsOnly) return@withContext PermissionResult.Granted
            val owner = Request(target, settingsOnly, missing ?: target.requiredPermissions.first(),
                if (settingsOnly || mode == PermissionPromptMode.Direct) Stage.LaunchRequested else Stage.AwaitingUser)
            publish(owner)
            try {
                owner.result.await()
            } finally {
                if (isCurrent(owner)) {
                    owner.abandoned = true
                    owner.result.cancel()
                    if (owner.stage == Stage.AwaitingUser || owner.stage == Stage.LaunchRequested) clear(owner)
                }
            }
        }

    internal fun confirm(owner: Request) {
        if (!isCurrent(owner) || owner.stage != Stage.AwaitingUser) return
        owner.explanationAccepted = true
        owner.stage = Stage.LaunchRequested
        publish(owner)
    }

    internal fun dismiss(owner: Request) {
        if (isCurrent(owner) && owner.stage == Stage.AwaitingUser) finish(owner, PermissionResult.Denied)
    }

    internal fun explainSettings(owner: Request): Boolean {
        if (!isCurrent(owner) || owner.stage != Stage.LaunchRequested || owner.settingsOnly || owner.explanationAccepted) return false
        owner.stage = Stage.AwaitingUser
        publish(owner)
        return true
    }

    internal fun beginLaunch(owner: Request, runtime: Boolean): Boolean {
        if (!isCurrent(owner) || owner.stage != Stage.LaunchRequested) return false
        owner.stage = if (runtime) Stage.WaitingRuntime else Stage.WaitingSettings
        owner.observedPause = false
        publish(owner)
        return true
    }

    internal fun runtimeStarted(owner: Request) {
        if (!isCurrent(owner) || owner.stage != Stage.WaitingRuntime || owner.runtimeLaunched) return
        owner.runtimeLaunched = true
        recordRequested(owner.missing)
    }

    internal fun runtimeReturned(owner: Request): Boolean {
        if (!isCurrent(owner) || owner.stage != Stage.WaitingRuntime) return false
        runtimeStarted(owner)
        if (!owner.abandoned && !isGranted(owner.missing)) recordDenied(owner.missing)
        recheck(owner)
        return true
    }

    internal fun markPaused(owner: Request) {
        if (isCurrent(owner) && owner.stage == Stage.WaitingSettings) owner.observedPause = true
    }

    internal fun settingsReturned(owner: Request, activityResult: Boolean = false) {
        if (!isCurrent(owner) || owner.stage != Stage.WaitingSettings) return
        if (!activityResult && !owner.observedPause) return
        recheck(owner)
    }

    internal fun launchFailed(owner: Request) {
        if (isCurrent(owner)) recheck(owner, allowNext = false)
    }

    internal fun cancelAll() {
        val owner = mutableRequest.value?.owner ?: return
        clear(owner)
        owner.result.cancel()
    }

    private fun recheck(owner: Request, allowNext: Boolean = true) {
        if (owner.abandoned) { clear(owner); return }
        val missing = owner.target.requiredPermissions.firstOrNull { !isGranted(it) }
        when {
            missing == null -> finish(owner, PermissionResult.Granted)
            allowNext && !owner.settingsOnly && missing != owner.missing -> {
                owner.missing = missing
                owner.stage = Stage.AwaitingUser
                owner.observedPause = false
                owner.runtimeLaunched = false
                owner.explanationAccepted = false
                publish(owner)
            }
            else -> finish(owner, PermissionResult.Denied)
        }
    }
    private fun finish(owner: Request, result: PermissionResult) {
        if (!isCurrent(owner)) return
        clear(owner)
        if (!owner.abandoned) owner.result.complete(result)
    }
    private fun clear(owner: Request) { if (isCurrent(owner)) mutableRequest.value = null }
    private fun isCurrent(owner: Request) = mutableRequest.value?.owner === owner
    private fun publish(owner: Request) { mutableRequest.value = Prompt(owner, owner.stage, owner.missing) }
}
