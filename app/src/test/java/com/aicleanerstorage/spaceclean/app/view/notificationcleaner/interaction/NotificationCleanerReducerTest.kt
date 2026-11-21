package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationCleanerReducerTest {
    @Test
    fun `scan started enters scanning`() {
        val started = NotificationCleanerReducer.scanStarted(NotificationCleanerUiState())

        assertEquals(NotificationCleanerPage.Scanning, started.page)
        assertEquals(true, started.scanInProgress)
        assertEquals(true, started.isLoading)
    }

    @Test
    fun `scan completed only moves from scanning to status`() {
        val scanning = scanningState()
        assertEquals(
            NotificationCleanerPage.Status,
            NotificationCleanerReducer.scanCompleted(scanning).page,
        )

        val status = scanning.copy(page = NotificationCleanerPage.Status, scanInProgress = false)
        assertEquals(status, NotificationCleanerReducer.scanCompleted(status))
    }

    @Test
    fun `scan cancelled clears flags only while scanning`() {
        val cancelled = NotificationCleanerReducer.scanCancelled(scanningState())

        assertEquals(false, cancelled.scanInProgress)
        assertEquals(false, cancelled.isLoading)
        assertEquals(NotificationCleanerPage.Scanning, cancelled.page)

        val status = NotificationCleanerUiState(page = NotificationCleanerPage.Status)
        assertEquals(status, NotificationCleanerReducer.scanCancelled(status))
    }

    @Test
    fun `notifications cleaned enters cleaning and clears counts`() {
        val cleaned =
            NotificationCleanerReducer.notificationsCleaned(
                NotificationCleanerUiState(
                    page = NotificationCleanerPage.Status,
                    blockedCount = 3,
                    blockedCountsByPackage = mapOf("com.a" to 3),
                ),
                cleanedCount = 3,
            )

        assertEquals(NotificationCleanerPage.Cleaning, cleaned.page)
        assertEquals(0, cleaned.blockedCount)
        assertEquals(emptyMap<String, Int>(), cleaned.blockedCountsByPackage)
        assertEquals(3, cleaned.cleanedCount)
    }

    @Test
    fun `cleaning finished moves to complete`() {
        val finished =
            NotificationCleanerReducer.cleaningFinished(
                NotificationCleanerUiState(
                    page = NotificationCleanerPage.Cleaning,
                    cleanedCount = 2,
                ),
            )

        assertEquals(NotificationCleanerPage.Complete, finished.page)
        assertEquals(false, finished.completionResultVisible)
    }

    @Test
    fun `completion display returns to status unless the target is complete`() {
        val complete =
            NotificationCleanerReducer.completionDisplayed(
                NotificationCleanerUiState(page = NotificationCleanerPage.Complete),
                NotificationCleanerPage.Complete,
            )
        assertEquals(NotificationCleanerPage.Complete, complete.page)
        assertEquals(true, complete.completionResultVisible)

        val status =
            NotificationCleanerReducer.completionDisplayed(
                NotificationCleanerUiState(page = NotificationCleanerPage.Complete),
                NotificationCleanerPage.Status,
            )
        assertEquals(NotificationCleanerPage.Status, status.page)
        assertEquals(false, status.completionResultVisible)
    }

    private fun scanningState(): NotificationCleanerUiState =
        NotificationCleanerUiState(
            page = NotificationCleanerPage.Scanning,
            isLoading = true,
            scanInProgress = true,
        )
}
