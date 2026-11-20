package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import java.lang.reflect.Proxy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLockMonitoringRuleTest {
    @Test
    fun monitoringRequiresEveryPrerequisite() {
        val ready = mapOf(
            "isPinSet" to true,
            "isMonitoringEnabled" to true,
            "lockedPackages" to setOf("locked.app"),
            "hasOverlayPermission" to true,
            "hasUsageAccess" to true,
        )
        fun repository(values: Map<String, Any>) = Proxy.newProxyInstance(
            AppLockRepository::class.java.classLoader,
            arrayOf(AppLockRepository::class.java),
        ) { _, method, _ -> values.getValue(method.name) } as AppLockRepository

        assertTrue(AppLockMonitoringRule.shouldMonitorApps(repository(ready)))
        for (condition in ready.keys) {
            val missing = if (condition == "lockedPackages") emptySet<String>() else false
            assertFalse(condition, AppLockMonitoringRule.shouldMonitorApps(repository(ready + (condition to missing))))
        }
    }
}
