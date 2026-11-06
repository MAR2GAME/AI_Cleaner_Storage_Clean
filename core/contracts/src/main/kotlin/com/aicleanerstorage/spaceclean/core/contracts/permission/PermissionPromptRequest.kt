package com.aicleanerstorage.spaceclean.core.contracts.permission

data class PermissionPromptRequest(
    val target: PermissionTarget,
    val missingPermission: PermissionType?,
)
