package com.aicleanerstorage.spaceclean.base.permission

import android.content.Context

/** 只读权限检查；申请与结果处理由 Route 负责。 */
interface PermissionChecker {
    fun isGranted(permission: AppPermission): Boolean
}

class ContextPermissionChecker(
    private val context: Context,
) : PermissionChecker {
    override fun isGranted(permission: AppPermission): Boolean = permission.isGranted(context)
}
