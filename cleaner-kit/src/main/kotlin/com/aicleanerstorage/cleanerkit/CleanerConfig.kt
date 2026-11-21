package com.aicleanerstorage.cleanerkit

import android.content.IntentSender

data class CleanerConfig(
    val authorizeMediaDeletion: (suspend (IntentSender) -> Boolean)? = null,
)
