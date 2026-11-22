package com.aicleanerstorage.spaceclean.app.permission

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionRequester
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * 用当前 Activity 的 [ActivityResultRegistry] 实现系统删除授权。
 *
 * 实例由 Koin 持有，data 只看到 [MediaDeletionRequester] 接口；
 * Activity 在 onCreate/onDestroy 中负责 attach/detach。
 */
internal class ActivityMediaDeletionRequester : MediaDeletionRequester {
    private val requests = Mutex()
    private var registry: ActivityResultRegistry? = null
    private var pending: CompletableDeferred<Boolean>? = null

    /** 绑定当前 Activity 的 result registry。 */
    fun attach(value: ActivityResultRegistry) {
        registry = value
    }

    /**
     * 解绑 result registry。
     *
     * 若此时还有等待中的授权，按“用户未确认”完成，避免协程永久挂起。
     */
    fun detach(value: ActivityResultRegistry) {
        if (registry === value) {
            registry = null
            pending?.complete(false)
        }
    }

    override suspend fun request(sender: IntentSender): Boolean =
        withContext(Dispatchers.Main.immediate) {
            requests.withLock {
                val host = registry ?: return@withLock false
                val result = CompletableDeferred<Boolean>()
                val launcher =
                    host.register(
                        "media-delete-" + UUID.randomUUID(),
                        ActivityResultContracts.StartIntentSenderForResult(),
                    ) { result.complete(it.resultCode == Activity.RESULT_OK) }
                pending = result
                try {
                    launcher.launch(IntentSenderRequest.Builder(sender).build())
                    result.await()
                } catch (cancelled: kotlinx.coroutines.CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    false
                } finally {
                    pending = null
                    launcher.unregister()
                }
            }
        }
}
