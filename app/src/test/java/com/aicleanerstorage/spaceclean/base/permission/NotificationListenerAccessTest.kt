package com.aicleanerstorage.spaceclean.base.permission

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationListenerAccessTest {
    private val current =
        "com.aicleanerstorage.spaceclean/" +
            "com.aicleanerstorage.spaceclean.data.local.notificationcleaner.StorageCleanNotificationListener"
    private val ownServices = setOf(current)

    @Test
    fun missingSettingIsNotGranted() {
        assertFalse(NotificationListenerAccess.isGranted(null, ownServices))
    }

    @Test
    fun otherPackageIsNotGranted() {
        val other = "com.other.app/com.other.app.OtherNotificationListener"

        assertFalse(NotificationListenerAccess.isGranted(other, ownServices))
    }

    @Test
    fun staleListenerFromOlderBuildIsNotGranted() {
        val stale =
            "com.aicleanerstorage.spaceclean/" +
                "com.aicleanerstorage.spaceclean.feature.notificationcleaner.StorageCleanNotificationListener"

        assertFalse(NotificationListenerAccess.isGranted(stale, ownServices))
    }

    @Test
    fun currentListenerIsGranted() {
        assertTrue(NotificationListenerAccess.isGranted(current, ownServices))
    }

    @Test
    fun anyDeclaredOwnListenerIsGranted() {
        val other =
            "com.aicleanerstorage.spaceclean/" +
                "com.aicleanerstorage.spaceclean.data.local.notificationcleaner.OtherListener"

        assertTrue(NotificationListenerAccess.isGranted(other, ownServices + other))
    }

    @Test
    fun shortComponentNameIsExpanded() {
        val short =
            "com.aicleanerstorage.spaceclean/.data.local.notificationcleaner.StorageCleanNotificationListener"

        assertTrue(NotificationListenerAccess.isGranted(short, ownServices))
    }

    @Test
    fun malformedEntriesAreIgnored() {
        assertFalse(NotificationListenerAccess.isGranted("::nonsense:", ownServices))
    }

    @Test
    fun emptyOwnServicesIsNeverGranted() {
        assertFalse(NotificationListenerAccess.isGranted(current, emptySet()))
    }
}
