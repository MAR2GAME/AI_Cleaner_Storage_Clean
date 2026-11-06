package com.aicleanerstorage.spaceclean.core.contracts.intent

class ExternalActivityTracker(
    val markLaunch: () -> Unit = {},
    val cancelLaunch: () -> Unit = {},
    val markReturn: () -> Unit = {},
)
