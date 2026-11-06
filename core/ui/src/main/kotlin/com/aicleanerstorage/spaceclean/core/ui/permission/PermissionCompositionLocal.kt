package com.aicleanerstorage.spaceclean.core.ui.permission

import androidx.compose.runtime.staticCompositionLocalOf
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
val LocalPermissionGateway = staticCompositionLocalOf<PermissionGateway> { error("Permission gateway is not available") }
