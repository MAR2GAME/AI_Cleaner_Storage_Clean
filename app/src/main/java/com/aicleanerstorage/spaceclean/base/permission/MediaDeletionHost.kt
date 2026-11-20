package com.aicleanerstorage.spaceclean.base.permission

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Only an active host activity can present a system media confirmation. */
internal object MediaDeletionHost {
    private val requests = Mutex()
    private var registry: ActivityResultRegistry? = null
    private var pending: CompletableDeferred<Boolean>? = null

    fun attach(value: ActivityResultRegistry) {
        registry = value
    }

    fun detach(value: ActivityResultRegistry) {
        if (registry === value) {
            registry = null
            pending?.complete(false)
        }
    }

    suspend fun authorize(sender: IntentSender): Boolean = withContext(Dispatchers.Main.immediate) {
        requests.withLock {
            val host = registry ?: return@withLock false
            val result = CompletableDeferred<Boolean>()
            val launcher = host.register(
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
