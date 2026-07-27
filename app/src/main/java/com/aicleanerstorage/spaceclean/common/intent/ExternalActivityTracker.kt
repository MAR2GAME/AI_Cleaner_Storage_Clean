package com.aicleanerstorage.spaceclean.common.intent

class ExternalActivityTracker(
    val markLaunch: () -> Unit = {},
    val cancelLaunch: () -> Unit = {},
    val markReturn: () -> Unit = {},
)
