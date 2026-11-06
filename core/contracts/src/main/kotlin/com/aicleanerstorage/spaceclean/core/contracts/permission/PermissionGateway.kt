package com.aicleanerstorage.spaceclean.core.contracts.permission

enum class PermissionResult { Granted, Denied, Busy }

interface PermissionGateway {
    fun isGranted(permission: PermissionType): Boolean
    suspend fun ensure(target: PermissionTarget, mode: PermissionPromptMode = PermissionPromptMode.Explained): PermissionResult
    suspend fun openSettings(permission: PermissionType): PermissionResult
}
