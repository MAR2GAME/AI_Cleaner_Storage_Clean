package com.aicleanerstorage.spaceclean.base.permission

/**
 * 读取平台数据时缺少必需权限的异常。
 *
 * 这是可恢复的控制流信号，不是崩溃：页面捕获后按 [permission] 走
 * [PermissionGate] 申请，或跳对应设置页。data 层只负责在读取前断言，
 * 不自己弹系统界面。
 *
 * @property permission 缺失的权限；页面据此决定文案和申请方式。
 */
class PermissionRequiredException(
    val permission: AppPermission,
) : SecurityException("Permission required: ${permission.name}")
