package com.aicleanerstorage.spaceclean.common.permission

data class PermissionPromptRequest(
    val target: PermissionTarget,
    val missingPermission: PermissionType?,
)
