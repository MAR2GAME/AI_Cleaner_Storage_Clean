package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.DefaultResultDisplayCategories
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanReducer
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanUiState
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleaningCancelled
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleaningCompleted
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleaningRequested
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkDeleteAuthorizationHandled
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkDeleteAuthorizationRequested
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkFailed
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkOperation
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkPermissionCheckDeferred
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkPermissionCheckRestored
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkPermissionCheckStarted
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkPermissionDenied
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkPreviewLoaded
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkResultAnimationCompleted
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkStopDialogDismissed
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkStopDialogShown
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.error.JunkError
import com.aicleanerstorage.spaceclean.domain.model.CleanupSummaryModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class JunkCleanReducerTest {
    @Test
    fun `selection creates new items without mutating previous state snapshot`() {
        val previous =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
            )
        val original = previous.groups.flatMap { it.items }.single()
        val changed = JunkCleanReducer.reduce(previous, JunkCleanAction.ItemToggled("cache"))
        assertTrue(original.isChecked)
        assertTrue(
            previous.groups
                .flatMap { it.items }
                .single()
                .isChecked,
        )
        assertFalse(
            changed.groups
                .flatMap { it.items }
                .single()
                .isChecked,
        )
        assertEquals(1, previous.selectedSummary.checkedCount)
        assertEquals(0, changed.selectedSummary.checkedCount)
    }

    @Test
    fun `permission restore returns entire snapshot only for matching check`() {
        val previous =
            JunkCleanReducer
                .reduce(
                    JunkCleanUiState(),
                    JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
                ).copy(error = UiText.Raw("previous message"), progress = 100f)
        JunkOperation.entries.forEach { request ->
            val checking = JunkCleanReducer.reduce(previous, JunkPermissionCheckStarted(request))
            val restore = JunkPermissionCheckRestored(request, previous)
            assertSame(previous, JunkCleanReducer.reduce(checking, restore))
            val otherRequest = JunkOperation.entries.first { it != request }
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
        val stale =
            JunkCleanUiState(
                phase = JunkCleanPhase.Failed(JunkOperation.Scan),
                progress = 80f,
                currentCategory = JunkCategory.CACHE,
                foundItemCount = 10,
                foundTotalSize = 2048L,
                formattedFoundSize = "2 KB",
            )
        val checking = JunkCleanReducer.reduce(stale, JunkPermissionCheckStarted(JunkOperation.Scan))
        assertEquals(JunkCleanPhase.CheckingPermission(JunkOperation.Scan), checking.phase)
        assertEquals(0f, checking.progress, 0f)
        assertNull(checking.currentCategory)
        assertEquals(0, checking.foundItemCount)
        assertEquals(0L, checking.foundTotalSize)
        assertEquals("0 B", checking.formattedFoundSize)
        val denied =
            JunkCleanReducer.reduce(
                stale.copy(
                    error = JunkError.ScanFailed.toUiText(),
                    showStopDialog = true,
                    stopDialogPhase = JunkCleanPhase.Scanning,
                ),
                JunkPermissionDenied(JunkOperation.Scan),
            )
        assertEquals(JunkCleanPhase.Idle, denied.phase)
        assertEquals(0f, denied.progress, 0f)
        assertNull(denied.currentCategory)
        assertEquals(0, denied.foundItemCount)
        assertEquals(0L, denied.foundTotalSize)
        assertEquals("0 B", denied.formattedFoundSize)
        assertNull(denied.error)
        assertFalse(denied.showStopDialog)
        assertNull(denied.stopDialogPhase)
    }

    @Test
    fun `clean denial and busy preserve restored preview including selection`() {
        val previous =
            JunkCleanReducer.reduce(
                JunkCleanUiState(),
                JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
            )
        val denied = JunkCleanReducer.reduce(previous, JunkPermissionDenied(JunkOperation.Clean))
        assertEquals(previous, denied)
        JunkOperation.entries.forEach { request ->
            assertSame(previous, JunkCleanReducer.reduce(previous, JunkPermissionCheckDeferred(request)))
        }
        assertFalse(previous.cleanResult.hasVisibleResult)
    }

    @Test
    fun `permission check purpose is stored only in phase and clean denial restores preview`() {
        val preview =
            JunkCleanReducer
                .reduce(
                    JunkCleanUiState(),
                    JunkPreviewLoaded(scanResult(file("cache", 100L, JunkCategory.CACHE))),
                ).copy(progress = 100f, foundItemCount = 1, foundTotalSize = 100L, formattedFoundSize = "100 B")
        val checking = JunkCleanReducer.reduce(preview, JunkPermissionCheckStarted(JunkOperation.Clean))
        assertEquals(JunkCleanPhase.CheckingPermission(JunkOperation.Clean), checking.phase)
        assertEquals(preview.copy(phase = checking.phase), checking)
        assertEquals(preview.groups, checking.groups)
        val denied = JunkCleanReducer.reduce(checking, JunkPermissionDenied(JunkOperation.Clean))
        assertEquals(preview, denied)
    }

    @Test
    fun `preview selects scanned files and tracks empty display categories`() {
        val result = scanResult(file("cache", 120L, JunkCategory.CACHE))

        val state = JunkCleanReducer.reduce(JunkCleanUiState(), JunkPreviewLoaded(result))

        assertEquals(JunkCleanPhase.Preview, state.phase)
        assertEquals(1, state.selectedSummary.checkedCount)
        assertEquals(120L, state.selectedSummary.checkedSize)
        assertTrue(
            state.groups
                .single()
                .items
                .single()
                .isChecked,
        )
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

        val oneUnchecked = JunkCleanReducer.reduce(preview, JunkCleanAction.ItemToggled("cache-1"))
        val allChecked =
            JunkCleanReducer.reduce(
                oneUnchecked,
                JunkCleanAction.CategoriesToggled(listOf(JunkCategory.CACHE)),
            )
        val allUnchecked =
            JunkCleanReducer.reduce(
                allChecked,
                JunkCleanAction.CategoriesToggled(listOf(JunkCategory.CACHE)),
            )

        assertEquals(1, oneUnchecked.selectedSummary.checkedCount)
        assertEquals(200L, oneUnchecked.selectedSummary.checkedSize)
        assertEquals(2, allChecked.selectedSummary.checkedCount)
        assertEquals(0, allUnchecked.selectedSummary.checkedCount)
        assertTrue(
            allUnchecked.groups
                .single()
                .items
                .none { it.isChecked },
        )
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
                JunkCleanReducer.reduce(state, JunkCleanAction.CategoriesToggled(listOf(category)))
            }

        val zeroByteFailure = JunkCleanReducer.reduce(preview, JunkCleaningRequested)
        val noSelectionFailure = JunkCleanReducer.reduce(emptyCategoriesCleared, JunkCleaningRequested)

        assertEquals(UiText.Res(R.string.result_zero_byte_selection_hint), zeroByteFailure.error)
        assertEquals(UiText.Res(R.string.result_select_at_least_one), noSelectionFailure.error)
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
                    CleanupSummaryModel(
                        freedSpace = 100L,
                        cleanedCount = 1,
                        failedCount = 0,
                        memoryFreedBytes = 20L,
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
        assertEquals(100L, animation.cleanResult.freedSpace)
        assertTrue(animation.cleanResult.hasVisibleResult)
        assertEquals(JunkCleanPhase.Complete, complete.phase)
    }

    @Test
    fun `completion and cancellation clear pending authorization count`() {
        val waiting = JunkCleanUiState(phase = JunkCleanPhase.AwaitingAuthorization, pendingAuthorizationCount = 3)
        val complete =
            JunkCleanReducer.reduce(
                waiting,
                JunkCleaningCompleted(
                    CleanupSummaryModel(freedSpace = 100L, cleanedCount = 1, failedCount = 3, memoryFreedBytes = 0L),
                ),
            )
        assertEquals(0, complete.pendingAuthorizationCount)
        assertEquals(0, JunkCleanReducer.reduce(waiting, JunkCleaningCancelled).pendingAuthorizationCount)
    }

    @Test
    fun `phase guarded actions do not mutate unrelated states`() {
        val scanning = JunkCleanUiState(phase = JunkCleanPhase.Scanning)

        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleanAction.ItemToggled("missing")))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleaningRequested))
        assertSame(scanning, JunkCleanReducer.reduce(scanning, JunkCleaningCancelled))
    }

    @Test
    fun `failure operation is expressed by the single phase`() {
        val scanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Scanning),
                JunkFailed(JunkError.ScanFailed, duringScan = true),
            )
        val cleanFailure =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Cleaning),
                JunkFailed(JunkError.CleanFailed),
            )

        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), scanFailure.phase)
        assertEquals(UiText.Res(R.string.scan_failed), scanFailure.error)
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Clean), cleanFailure.phase)
        assertEquals(UiText.Res(R.string.result_clean_error), cleanFailure.error)
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

    @Test
    fun `stopping after five deletions preserves remaining selection and actual bytes`() {
        val files = (1..7).map { file("file" + it, 10L, JunkCategory.CACHE) }
        val remaining = files.drop(5)
        val stopped =
            JunkCleanReducer.reduce(
                JunkCleanUiState(phase = JunkCleanPhase.Cleaning),
                JunkCleaningStopped(scanResult(*remaining.toTypedArray()), setOf(remaining.first()), CleanupSummaryModel(50L, 5, 0, 0L)),
            )
        assertEquals(JunkCleanPhase.Preview, stopped.phase)
        assertEquals(5, stopped.stoppedSummary?.cleanedCount)
        assertEquals(50L, stopped.stoppedSummary?.freedSpace)
        assertEquals(remaining, stopped.groups.flatMap { group -> group.items.map { it.junkFile } })
        assertEquals(1, stopped.selectedSummary.checkedCount)
    }

    private fun scanResult(vararg files: JunkFile): ScanResult = ScanResult(files.toList())

    private fun file(
        id: String,
        size: Long,
        category: JunkCategory,
    ) = JunkFile(
        id = id,
        filePath = "/$id",
        fileName = id,
        fileSize = size,
        category = category,
        lastModified = 0L,
    )
}
