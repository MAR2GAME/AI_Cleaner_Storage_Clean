package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent

sealed interface AppUsageOperation {
    data object LoadUsageStats : AppUsageOperation
}

