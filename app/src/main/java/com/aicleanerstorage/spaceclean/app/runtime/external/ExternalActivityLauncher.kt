package com.aicleanerstorage.spaceclean.app.runtime.external

class ExternalActivityLauncher(
    val markLaunch: () -> Unit = {},
    val cancelLaunch: () -> Unit = {},
    val markReturn: () -> Unit = {},
)
