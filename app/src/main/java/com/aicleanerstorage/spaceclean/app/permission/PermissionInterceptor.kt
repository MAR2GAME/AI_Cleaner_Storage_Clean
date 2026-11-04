package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy

class PermissionInterceptor(
    private val gateway: PermissionGateway,
) : IntentInterceptor {

    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy !is PermissionNeed) return InterceptResult.NotHandled

        return when (gateway.ensure(policy)) {
            PermissionResult.Granted -> InterceptResult.Continue
            PermissionResult.Denied -> InterceptResult.Block
            PermissionResult.Busy -> InterceptResult.Busy
        }
    }
}
