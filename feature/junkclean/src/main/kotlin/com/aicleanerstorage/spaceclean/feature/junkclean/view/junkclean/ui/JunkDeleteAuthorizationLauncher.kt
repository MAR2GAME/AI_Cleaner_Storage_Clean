package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import kotlinx.coroutines.CancellationException

fun launchJunkDeleteAuthorization(
    uris: List<String>,
    createRequest: (List<String>) -> IntentSenderRequest?,
    launch: (IntentSenderRequest) -> Unit,
    onUnavailable: () -> Unit,
    sdkInt: Int = Build.VERSION.SDK_INT,
) {
    val launched = try {
        val request = if (sdkInt >= Build.VERSION_CODES.R && uris.isNotEmpty()) {
            createRequest(uris)
        } else {
            null
        }
        if (request == null) {
            false
        } else {
            launch(request)
            true
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        false
    }
    if (!launched) onUnavailable()
}

fun createJunkDeleteRequest(context: Context, uris: List<String>): IntentSenderRequest? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || uris.isEmpty()) return null
    val request = MediaStore.createDeleteRequest(context.contentResolver, uris.map(Uri::parse))
    return IntentSenderRequest.Builder(request.intentSender).build()
}
