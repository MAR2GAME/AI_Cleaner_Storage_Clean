package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import com.aicleanerstorage.spaceclean.feature.junkclean.*

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanupSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class JunkCleanReducerTest {
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
        val checking = JunkCleanReducer.reduce(stale, JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Scan))
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
            JunkCleanAction.PermissionDenied(JunkPermissionRequest.Scan),
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
            JunkCleanAction.PreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        )
        val denied = JunkCleanReducer.reduce(previous, JunkCleanAction.PermissionDenied(JunkPermissionRequest.Clean))
        assertEquals(previous, denied)
        JunkPermissionRequest.entries.forEach { request ->
            assertSame(previous, JunkCleanReducer.reduce(previous, JunkCleanAction.PermissionBusy(request)))
        }
        assertFalse(previous.cleanResult.hasVisibleResult)
    }

    @Test
    fun `permission check purpose is stored only in phase and clean denial restores preview`() {
        val preview = JunkCleanReducer.reduce(
            JunkCleanUiState(), JunkCleanAction.PreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
        ).copy(progress = 100f, foundItemCount = 1, foundTotalSize = 100L, formattedFoundSize = "100 B")
        val checking = JunkCleanReducer.reduce(preview, JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Clean))
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Clean), checking.phase)
        assertEquals(preview.copy(phase = checking.phase), checking)
        assertEquals(preview.groups, checking.groups)
        val denied = JunkCleanReducer.reduce(checking, JunkCleanAction.PermissionDenied(JunkPermissionRequest.Clean))
        assertEquals(preview, denied)
    }

    @Test
    fun `preview selects scanned files and tracks empty display categories`() {
        val result = scanResult(file("cache", 120L, JunkCategory.CACHE))

        val state = JunkCleanReducer.reduce(JunkCleanUiState(), JunkCleanAction.PreviewLoaded(result))

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
                JunkCleanAction.PreviewLoaded(
                    scanResult(
                        file("cache-1", 100L, JunkCategory.CACHE),
                        file("cache-2", 200L, JunkCategory.CACHE),
                    ),
                ),
            )

        val oneUnchecked = JunkCleanReducer.reduce(preview, JunkCleanAction.ToggleItem("cache-1"))
        val allChecked =
            JunkCleanReducer.reduce(
                oneUnchecked,
                JunkCleanAction.ToggleCategories(listOf(JunkCategory.CACHE)),
            )
        val allUnchecked =
            JunkCleanReducer.reduce(
                allChecked,
                JunkCleanAction.ToggleCategories(listOf(JunkCategory.CACHE)),
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
                JunkCleanAction.PreviewLoaded(ScanResult.EMPTY),
            )
        val emptyCategoriesCleared =
            DefaultResultDisplayCategories.fold(preview) { state, category ->
                JunkCleanReducer.reduce(state, JunkCleanAction.ToggleCategories(listOf(category)))
            }

        val zeroByteFailure = JunkCleanReducer.reduce(preview, JunkCleanAction.CleaningRequested)
        val noSelectionFailure = JunkCleanReducer.reduce(emptyCategoriesCleared, JunkCleanAction.CleaningRequested)

        assertEquals(R.string.result_zero_byte_selection_hint, zeroByteFailure.errorMessageRes)
        assertEquals(R.string.result_select_at_least_one, noSelectionFailure.errorMessageRes)
    }

    @Test
    fun `cleaning follows authorization animation and complete phases`() {
        val preview =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkCleanAction.PreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
            )
        val cleaning = JunkCleanReducer.reduce(preview, JunkCleanAction.CleaningRequested)
        val authorization =
            JunkCleanReducer.reduce(cleaning, JunkCleanAction.DeleteAuthorizationRequested("confirm"))
        val resumed = JunkCleanReducer.reduce(authorization, JunkCleanAction.DeleteAuthorizationHandled)
        val animation =
            JunkCleanReducer.reduce(
                resumed,
                JunkCleanAction.CleaningCompleted(
                    CleanupSummary(
                        freedSpace = 100L,
                        cleanedCount = 1,
                        failedCount = 0,
                        memoryFreedBytes = 20L,
                        memoryProcessesKilled = 1,
                    ),
                ),
            )
        val complete = JunkCleanReducer.reduce(animation, JunkCleanAction.ResultAnimationCompleted)

        assertEquals(JunkCleanPhase.Cleaning, cleaning.phase)
        assertEquals(JunkCleanPhase.AwaitingAuthorization, authorization.phase)
        assertEquals("confirm", authorization.awaitingAuthorizationMessage)
        assertEquals(JunkCleanPhase.Cleaning, resumed.phase)
        assertNull(resumed.awaitingAuthorizationMessage)
        assertEquals(JunkCleanPhase.CompleteAnimation, animation.phase)
        assertEquals(120L, animation.cleanResult.totalFreedBytes)
        assertTrue(animation.cleanResult.hasVisibleResult)
        assertEquals(JunkCleanPhase.Complete, complete.phase)
    }

    @Test
    fun `phase guarded actions do not mutate unrelated states`() {
        val scanning = JunkCleanUiState(phase = JunkCleanPhase.Scanning)

        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleanAction.ToggleItem("missing")))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleanAction.CleaningRequested))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleanAction.CleaningCancelled))
    }

    @Test
    fun `failure operation is expressed by the single phase`() {
        val scanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Scanning),
                JunkCleanAction.Failed(R.string.scan_failed, "scan", duringScan = true),
            )
        val cleanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Cleaning),
                JunkCleanAction.Failed(R.string.result_clean_error, "clean"),
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
                JunkCleanAction.StopDialogShown(JunkCleanPhase.Scanning),
            )
        val dismissed = JunkCleanReducer.reduce(shown, JunkCleanAction.StopDialogDismissed)

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
