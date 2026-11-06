package com.aicleanerstorage.spaceclean.core.contracts.intent

sealed interface ExecutionResult<out R> {
    data class Accepted<R>(val requestId: String, val value: R) : ExecutionResult<R>
    data class Rejected(val requestId: String, val reason: String) : ExecutionResult<Nothing>
    data class Busy(val requestId: String) : ExecutionResult<Nothing>
    data class Cancelled(val requestId: String) : ExecutionResult<Nothing>
    data class Failed(
        val requestId: String,
        val error: Throwable,
    ) : ExecutionResult<Nothing>
}
