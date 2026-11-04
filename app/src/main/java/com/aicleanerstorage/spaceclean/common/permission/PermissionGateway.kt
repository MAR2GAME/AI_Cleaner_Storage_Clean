package com.aicleanerstorage.spaceclean.common.permission

import com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy

enum class PermissionNeed(val permission: PermissionType) : IntentPolicy {
    StorageFiles(PermissionType.StorageFiles);
    override val priority: Int = 100
}

enum class PermissionResult { Granted, Denied, Busy }

interface PermissionGateway {
    fun isGranted(permission: PermissionType): Boolean
    suspend fun ensure(target: PermissionTarget, mode: PermissionPromptMode = PermissionPromptMode.Explained): PermissionResult
    suspend fun openSettings(permission: PermissionType): PermissionResult
    suspend fun ensure(need: PermissionNeed): PermissionResult = ensure(PermissionTarget.Permission(need.permission))
}
