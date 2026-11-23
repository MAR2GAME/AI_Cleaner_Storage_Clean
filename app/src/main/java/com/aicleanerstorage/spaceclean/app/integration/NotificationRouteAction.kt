package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

data class NotificationRouteAction(
    val id: String,
    val destination: AppDestination,
    val source: String,
    val scene: String,
    val waitForForegroundAd: Boolean,
)
