package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation

import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanPhase
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkOperation
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanUiState
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPermissionRequest
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPermissionCheckStarted
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPermissionCheckRestored
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPermissionDenied
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPermissionBusy
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkPreviewLoaded
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleaningRequested
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkDeleteAuthorizationRequested
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkDeleteAuthorizationHandled
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleaningCompleted
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkFailed
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkResultAnimationCompleted
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleaningCancelled
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkStopDialogShown
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkStopDialogDismissed
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanIntent
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.DefaultResultDisplayCategories
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanReducer


import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult

import com.aicleanerstorage.spaceclean.core.ui.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class JunkCleanReducerTest {
    @Test
    fun `selection creates new items without mutating previous state snapshot`() {
        val previous = JunkCleanReducer.reduce(
            JunkCleanUiState(),
            JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        )
        val original = previous.groups.flatMap { it.items }.single()
        val changed = JunkCleanReducer.reduce(previous, JunkCleanIntent.ToggleItem("cache"))
        assertTrue(original.isChecked)
        assertTrue(previous.groups.flatMap { it.items }.single().isChecked)
        assertFalse(changed.groups.flatMap { it.items }.single().isChecked)
        assertEquals(1, previous.selectedSummary.checkedCount)
        assertEquals(0, changed.selectedSummary.checkedCount)
    }

    @Test
    fun `permission restore returns entire snapshot only for matching check`() {
        val previous = JunkCleanReducer.reduce(
            JunkCleanUiState(),
            JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        ).copy(errorMessage = "previous message", progress = 100f)
        JunkPermissionRequest.entries.forEach { request ->
            val checking = JunkCleanReducer.reduce(previous, JunkPermissionCheckStarted(request))
            val restore = JunkPermissionCheckRestored(request, previous)
            assertSame(previous, JunkCleanReducer.reduce(checking, restore))
            val otherRequest = JunkPermissionRequest.entries.first { it != request }
            assertSame(
                checking,
                JunkCleanReducer.reduce(checking, JunkPermissionCheckRestored(otherRequest, previous)),
            )
            val scanning = previous.copy(phase = JunkCleanPhase.Scanning)
            assertSame(scanning, JunkCleanReducer.reduce(scanning, restore))
        }
    }

    @Test
    fun `scan denial exits without error and clears stop dialog`() {
        val stale = JunkCleanUiState(
            phase = JunkCleanPhase.Failed(JunkOperation.Scan),
            progress = 80f,
            currentCategory = JunkCategory.CACHE,
            foundItemCount = 10,
            foundTotalSize = 2048L,
            formattedFoundSize = "2 KB",
        )
        val checking = JunkCleanReducer.reduce(stale, JunkPermissionCheckStarted(JunkPermissionRequest.Scan))
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Scan), checking.phase)
        assertEquals(0f, checking.progress, 0f)
        assertNull(checking.currentCategory)
        assertEquals(0, checking.foundItemCount)
        assertEquals(0L, checking.foundTotalSize)
        assertEquals("0 B", checking.formattedFoundSize)
        val denied = JunkCleanReducer.reduce(
            stale.copy(
                errorMessageRes = R.string.scan_failed,
                errorMessage = "previous failure",
                showStopDialog = true,
                stopDialogPhase = JunkCleanPhase.Scanning,
            ),
            JunkPermissionDenied(JunkPermissionRequest.Scan),
        )
        assertEquals(JunkCleanPhase.Idle, denied.phase)
        assertEquals(0f, denied.progress, 0f)
        assertNull(denied.currentCategory)
        assertEquals(0, denied.foundItemCount)
        assertEquals(0L, denied.foundTotalSize)
        assertEquals("0 B", denied.formattedFoundSize)
        assertNull(denied.errorMessageRes)
        assertNull(denied.errorMessage)
        assertFalse(denied.showStopDialog)
        assertNull(denied.stopDialogPhase)
    }

    @Test
    fun `clean denial and busy preserve restored preview including selection`() {
        val previous = JunkCleanReducer.reduce(
            JunkCleanUiState(),
            JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        )
        val denied = JunkCleanReducer.reduce(previous, JunkPermissionDenied(JunkPermissionRequest.Clean))
        assertEquals(previous, denied)
        JunkPermissionRequest.entries.forEach { request ->
            assertSame(previous, JunkCleanReducer.reduce(previous, JunkPermissionBusy(request)))
        }
        assertFalse(previous.cleanResult.hasVisibleResult)
    }

    @Test
    fun `permission check purpose is stored only in phase and clean denial restores preview`() {
        val preview = JunkCleanReducer.reduce(
            JunkCleanUiState(), JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        ).copy(progress = 100f, foundItemCount = 1, foundTotalSize = 100L, formattedFoundSize = "100 B")
        val checking = JunkCleanReducer.reduce(preview, JunkPermissionCheckStarted(JunkPermissionRequest.Clean))
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Clean), checking.phase)
        assertEquals(preview.copy(phase = checking.phase), checking)
        assertEquals(preview.groups, checking.groups)
        val denied = JunkCleanReducer.reduce(checking, JunkPermissionDenied(JunkPermissionRequest.Clean))
        assertEquals(preview, denied)
    }

    @Test
    fun `preview selects scanned files and tracks empty display categories`() {
        val result = scanResult(file("cache", 120L, JunkCategory.CACHE))

        val state = JunkCleanReducer.reduce(JunkCleanUiState(), JunkPreviewLoaded(result))

        assertEquals(JunkCleanPhase.Preview, state.phase)
        assertEquals(1, state.selectedSummary.checkedCount)
        assertEquals(120L, state.selectedSummary.checkedSize)
        assertTrue(state.groups.single().items.single().isChecked)
        assertFalse(JunkCategory.CACHE in state.checkedEmptyCategories)
        assertTrue(JunkCategory.TEMP_FILE in state.checkedEmptyCategories)
    }

    @Test
    fun `item and category toggles update one selection summary`() {
        val preview =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkPreviewLoaded(
                    scanResult(
                        file("cache-1", 100L, JunkCategory.CACHE),
                        file("cache-2", 200L, JunkCategory.CACHE),
                    ),
                ),
            )

        val oneUnchecked = JunkCleanReducer.reduce(preview, JunkCleanIntent.ToggleItem("cache-1"))
        val allChecked =
            JunkCleanReducer.reduce(
                oneUnchecked,
                JunkCleanIntent.ToggleCategories(listOf(JunkCategory.CACHE)),
            )
        val allUnchecked =
            JunkCleanReducer.reduce(
                allChecked,
                JunkCleanIntent.ToggleCategories(listOf(JunkCategory.CACHE)),
            )

        assertEquals(1, oneUnchecked.selectedSummary.checkedCount)
        assertEquals(200L, oneUnchecked.selectedSummary.checkedSize)
        assertEquals(2, allChecked.selectedSummary.checkedCount)
        assertEquals(0, allUnchecked.selectedSummary.checkedCount)
        assertTrue(allUnchecked.groups.single().items.none { it.isChecked })
    }

    @Test
    fun `empty category toggles independently and changes cleaning validation`() {
        val preview =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkPreviewLoaded(ScanResult.EMPTY),
            )
        val emptyCategoriesCleared =
            DefaultResultDisplayCategories.fold(preview) { state, category ->
                JunkCleanReducer.reduce(state, JunkCleanIntent.ToggleCategories(listOf(category)))
            }

        val zeroByteFailure = JunkCleanReducer.reduce(preview, JunkCleaningRequested)
        val noSelectionFailure = JunkCleanReducer.reduce(emptyCategoriesCleared, JunkCleaningRequested)

        assertEquals(R.string.result_zero_byte_selection_hint, zeroByteFailure.errorMessageRes)
        assertEquals(R.string.result_select_at_least_one, noSelectionFailure.errorMessageRes)
    }

    @Test
    fun `cleaning follows authorization animation and complete phases`() {
        val preview =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
            )
        val cleaning = JunkCleanReducer.reduce(preview, JunkCleaningRequested)
        val authorization =
            JunkCleanReducer.reduce(cleaning, JunkDeleteAuthorizationRequested(2))
        val resumed = JunkCleanReducer.reduce(authorization, JunkDeleteAuthorizationHandled)
        val animation =
            JunkCleanReducer.reduce(
                resumed,
                JunkCleaningCompleted(
                    CleanupSummary(
                        freedSpace = 100L,
                        cleanedCount = 1,
                        failedCount = 0,
                        memoryFreedBytes = 20L,
                        memoryProcessesKilled = 1,
                    ),
                ),
            )
        val complete = JunkCleanReducer.reduce(animation, JunkResultAnimationCompleted)

        assertEquals(JunkCleanPhase.Cleaning, cleaning.phase)
        assertEquals(JunkCleanPhase.AwaitingAuthorization, authorization.phase)
        assertEquals(2, authorization.pendingAuthorizationCount)
        assertEquals(JunkCleanPhase.Cleaning, resumed.phase)
        assertEquals(0, resumed.pendingAuthorizationCount)
        assertEquals(0, animation.pendingAuthorizationCount)
        assertEquals(JunkCleanPhase.CompleteAnimation, animation.phase)
        assertEquals(120L, animation.cleanResult.totalFreedBytes)
        assertTrue(animation.cleanResult.hasVisibleResult)
        assertEquals(JunkCleanPhase.Complete, complete.phase)
    }

    @Test
    fun `completion and cancellation clear pending authorization count`() {
        val waiting = JunkCleanUiState(phase = JunkCleanPhase.AwaitingAuthorization, pendingAuthorizationCount = 3)
        val complete = JunkCleanReducer.reduce(
            waiting,
            JunkCleaningCompleted(
                CleanupSummary(freedSpace = 100L, cleanedCount = 1, failedCount = 3, memoryFreedBytes = 0L, memoryProcessesKilled = 0),
            ),
        )
        assertEquals(0, complete.pendingAuthorizationCount)
        assertEquals(0, JunkCleanReducer.reduce(waiting, JunkCleaningCancelled).pendingAuthorizationCount)
    }

    @Test
    fun `phase guarded actions do not mutate unrelated states`() {
        val scanning = JunkCleanUiState(phase = JunkCleanPhase.Scanning)

        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleanIntent.ToggleItem("missing")))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleaningRequested))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleaningCancelled))
    }

    @Test
    fun `failure operation is expressed by the single phase`() {
        val scanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Scanning),
                JunkFailed(R.string.scan_failed, "scan", duringScan = true),
            )
        val cleanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Cleaning),
                JunkFailed(R.string.result_clean_error, "clean"),
            )

        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), scanFailure.phase)
        assertEquals("scan", scanFailure.errorMessage)
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Clean), cleanFailure.phase)
        assertEquals("clean", cleanFailure.errorMessage)
    }

    @Test
    fun `stop dialog preserves the interrupted phase until dismissed`() {
        val shown =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Preview),
                JunkStopDialogShown(JunkCleanPhase.Scanning),
            )
        val dismissed = JunkCleanReducer.reduce(shown, JunkStopDialogDismissed)

        assertTrue(shown.showStopDialog)
        assertEquals(JunkCleanPhase.Scanning, shown.stopDialogPhase)
        assertFalse(dismissed.showStopDialog)
        assertNull(dismissed.stopDialogPhase)
    }

    private fun scanResult(vararg files: JunkFile): ScanResult {
        val items = files.toList()
        return ScanResult(
            junkFiles = items,
            totalSize = items.sumOf { it.fileSize },
            totalCount = items.size,
            categoryBreakdown = items.groupBy { it.category },
        )
    }

    private fun file(id: String, size: Long, category: JunkCategory) =
        JunkFile(
            id = id,
            filePath = "/$id",
            fileName = id,
            fileSize = size,
            category = category,
            lastModified = 0L,
        )
}
