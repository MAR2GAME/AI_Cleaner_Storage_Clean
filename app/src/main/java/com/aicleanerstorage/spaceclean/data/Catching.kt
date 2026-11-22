package com.aicleanerstorage.spaceclean.data

import kotlinx.coroutines.CancellationException

/**
 * 把平台异常收口成 Result；取消必须继续传播，不能当成失败。
 */
internal suspend fun <T> catching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
