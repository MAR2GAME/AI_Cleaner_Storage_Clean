package com.aicleanerstorage.spaceclean.core.presentation.intent

import java.util.UUID

fun newIntentRequestId(): String = UUID.randomUUID().toString()
