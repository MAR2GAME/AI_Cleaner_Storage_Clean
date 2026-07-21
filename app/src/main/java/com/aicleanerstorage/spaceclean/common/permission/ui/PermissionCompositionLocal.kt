package com.aicleanerstorage.spaceclean.common.permission.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.aicleanerstorage.spaceclean.common.permission.AppPermissionCoordinator

val LocalPermissionCoordinator =
    staticCompositionLocalOf<AppPermissionCoordinator> { error("Permission coordinator is not available") }
