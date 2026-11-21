package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

class FileManagerPermissionState(
    val granted: Boolean,
) {
    var leavingPage by mutableStateOf(false)
        private set

    fun markLeaving() {
        leavingPage = true
    }
}

fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.resetToRoot(AppDestination.Main())
}

fun FileManagerPermissionState.leaveBack(router: AppNavigator) {
    if (leavingPage) return
    markLeaving()
    router.back()
}
