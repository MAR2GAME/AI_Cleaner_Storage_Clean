package com.aicleanerstorage.spaceclean.core.presentation

interface IntentInterceptor {
    suspend fun intercept(policy: IntentPolicy): InterceptResult
}
