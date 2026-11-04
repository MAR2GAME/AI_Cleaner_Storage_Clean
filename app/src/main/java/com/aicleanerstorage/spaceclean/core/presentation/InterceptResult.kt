package com.aicleanerstorage.spaceclean.core.presentation

sealed interface InterceptResult {
    data object NotHandled : InterceptResult
    data object Continue : InterceptResult
    data object Block : InterceptResult
    data object Busy : InterceptResult
}
