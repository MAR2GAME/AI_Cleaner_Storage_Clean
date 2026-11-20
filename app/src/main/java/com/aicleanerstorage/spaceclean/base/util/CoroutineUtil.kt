package com.aicleanerstorage.spaceclean.base.util

import kotlinx.coroutines.CancellationException

object CoroutineUtil {
    suspend inline fun <T> runSuspendCatching(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            Result.failure(error)
        }
}
