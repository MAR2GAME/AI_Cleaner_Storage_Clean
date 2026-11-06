package com.aicleanerstorage.spaceclean.core.contracts.intent

data class IntentRequest<I>(
    val id: String,
    val source: String,
    val intent: I,
)
