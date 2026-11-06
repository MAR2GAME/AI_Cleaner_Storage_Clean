package com.aicleanerstorage.spaceclean.app.permission

import android.content.Context
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult

class PermissionGate<I>(
    context: Context,
    private val requiredPermissions: (I) -> List<String>,
) : IntentGate<I> {
    private val appContext = context.applicationContext

    override suspend fun check(
        request: IntentRequest<I>,
        ui: UiRequestPort,
    ): GateResult {
        val missing = requiredPermissions(request.intent)
            .distinct()
            .filterNot(::isGranted)
        if (missing.isEmpty()) return GateResult.Allow

        return when (ui.request(UiRequest.Permission(request.id, missing))) {
            UiResult.Granted -> {
                if (missing.all(::isGranted)) GateResult.Allow
                else GateResult.Deny("permission_not_granted")
            }
            UiResult.Cancelled -> GateResult.Cancelled
            UiResult.Denied -> GateResult.Deny("permission_denied")
            UiResult.Failed -> GateResult.Deny("permission_request_failed")
            else -> GateResult.Deny("invalid_permission_result")
        }
    }

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(appContext, permission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
}
