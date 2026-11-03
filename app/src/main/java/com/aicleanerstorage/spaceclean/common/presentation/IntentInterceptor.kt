package com.aicleanerstorage.spaceclean.common.presentation

interface IntentInterceptor {
    suspend fun intercept(policy: IntentPolicy): InterceptResult
}
