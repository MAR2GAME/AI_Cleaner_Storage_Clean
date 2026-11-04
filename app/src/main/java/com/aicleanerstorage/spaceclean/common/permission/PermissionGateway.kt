package com.aicleanerstorage.spaceclean.common.permission

import com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy

enum class PermissionNeed(
    val permission: PermissionType,
) : IntentPolicy {
    StorageFiles(PermissionType.StorageFiles);

    override val priority: Int = 100
}

enum class PermissionResult {
    Granted,
    Denied,
    Busy,
}

interface PermissionGateway {
    suspend fun ensure(need: PermissionNeed): PermissionResult
}
