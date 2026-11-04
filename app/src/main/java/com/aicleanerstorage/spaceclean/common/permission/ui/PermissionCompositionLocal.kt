package com.aicleanerstorage.spaceclean.common.permission.ui
import androidx.compose.runtime.staticCompositionLocalOf
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
val LocalPermissionGateway = staticCompositionLocalOf<PermissionGateway> { error("Permission gateway is not available") }
