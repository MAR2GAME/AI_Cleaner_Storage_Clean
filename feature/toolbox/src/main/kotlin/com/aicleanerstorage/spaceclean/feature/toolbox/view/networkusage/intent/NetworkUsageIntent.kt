package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent

sealed interface NetworkUsageOperation {
    data object LoadStats : NetworkUsageOperation
}
