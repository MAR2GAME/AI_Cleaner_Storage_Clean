package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionPromptMode

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget

import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

import android.content.Context
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
) : PermissionGateway {
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

    internal class Request(val target: PermissionTarget, val settingsOnly: Boolean) {
        val result = CompletableDeferred<PermissionResult>()
        var abandoned = false
        var observedPause = false
        var runtimeLaunched = false
        var explanationAccepted = false
    }

    internal data class Prompt(val owner: Request, val stage: Stage, val missing: PermissionType)
    private val mutableRequest = MutableStateFlow<Prompt?>(null)
    internal val request = mutableRequest.asStateFlow()

    override fun isGranted(permission: PermissionType): Boolean = try {
        checkGranted(permission)
    } catch (error: CancellationException) { throw error } catch (_: Exception) { false }

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
            val owner = Request(target, settingsOnly)
            mutableRequest.value = Prompt(
                owner = owner,
                stage = if (settingsOnly || mode == PermissionPromptMode.Direct) Stage.LaunchRequested else Stage.AwaitingUser,
                missing = missing ?: target.requiredPermissions.first(),
            )
            try {
                owner.result.await()
            } finally {
                val current = mutableRequest.value
                if (current?.owner === owner) {
                    owner.abandoned = true
                    owner.result.cancel()
                    if (current.stage == Stage.AwaitingUser || current.stage == Stage.LaunchRequested) clear(owner)
                }
            }
        }

    internal fun confirm(prompt: Prompt) {
        val current = current(prompt, Stage.AwaitingUser) ?: return
        current.owner.explanationAccepted = true
        mutableRequest.value = current.copy(stage = Stage.LaunchRequested)
    }

    internal fun dismiss(prompt: Prompt) {
        current(prompt, Stage.AwaitingUser)?.let { finish(it.owner, PermissionResult.Denied) }
    }

    internal fun explainSettings(prompt: Prompt): Boolean {
        val current = current(prompt, Stage.LaunchRequested) ?: return false
        if (current.owner.settingsOnly || current.owner.explanationAccepted) return false
        mutableRequest.value = current.copy(stage = Stage.AwaitingUser)
        return true
    }

    internal fun beginLaunch(prompt: Prompt, runtime: Boolean): Boolean {
        val current = current(prompt, Stage.LaunchRequested) ?: return false
        current.owner.observedPause = false
        mutableRequest.value = current.copy(stage = if (runtime) Stage.WaitingRuntime else Stage.WaitingSettings)
        return true
    }

    internal fun runtimeStarted(prompt: Prompt) {
        val current = current(prompt, Stage.WaitingRuntime) ?: return
        if (current.owner.runtimeLaunched) return
        current.owner.runtimeLaunched = true
        recordRequested(current.missing)
    }

    internal fun runtimeReturned(prompt: Prompt): Boolean {
        val current = current(prompt, Stage.WaitingRuntime) ?: return false
        runtimeStarted(current)
        val notify = !current.owner.abandoned
        if (notify && !isGranted(current.missing)) recordDenied(current.missing)
        recheck(current)
        return notify
    }

    internal fun markPaused(prompt: Prompt) {
        current(prompt, Stage.WaitingSettings)?.owner?.observedPause = true
    }

    internal fun settingsReturned(prompt: Prompt, activityResult: Boolean = false) {
        val current = current(prompt, Stage.WaitingSettings) ?: return
        if (!activityResult && !current.owner.observedPause) return
        recheck(current)
    }

    internal fun launchFailed(prompt: Prompt) {
        current(prompt)?.let { recheck(it, allowNext = false) }
    }

    internal fun cancelAll() {
        val owner = mutableRequest.value?.owner ?: return
        clear(owner)
        owner.result.cancel()
    }

    private fun recheck(prompt: Prompt, allowNext: Boolean = true) {
        val owner = prompt.owner
        if (owner.abandoned) { clear(owner); return }
        val missing = owner.target.requiredPermissions.firstOrNull { !isGranted(it) }
        when {
            missing == null -> finish(owner, PermissionResult.Granted)
            allowNext && !owner.settingsOnly && missing != prompt.missing -> {
                owner.observedPause = false
                owner.runtimeLaunched = false
                owner.explanationAccepted = false
                mutableRequest.value = prompt.copy(stage = Stage.AwaitingUser, missing = missing)
            }
            else -> finish(owner, PermissionResult.Denied)
        }
    }

    private fun finish(owner: Request, result: PermissionResult) {
        if (mutableRequest.value?.owner !== owner) return
        clear(owner)
        if (!owner.abandoned) owner.result.complete(result)
    }

    private fun clear(owner: Request) {
        if (mutableRequest.value?.owner === owner) mutableRequest.value = null
    }

    private fun current(prompt: Prompt, stage: Stage? = null): Prompt? =
        mutableRequest.value?.takeIf {
            it.owner === prompt.owner && it.missing == prompt.missing && (stage == null || it.stage == stage)
        }
}
