package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteOutcome
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteResult
import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateFilesWarningStateTest {
    @Test
    fun `accepting warning updates state and feature preferences`() {
        val preferences = InMemorySettingsRepository()
        val viewModel =
            DuplicateFilesManagerViewModel(
                repository = UnusedFileRepository,
                settingsRepository = preferences,
                ioDispatcher = Dispatchers.Unconfined,
            )

        assertTrue(viewModel.state.value.showWarning)

        viewModel.dispatch(FileManagerAction.WarningConfirmed)

        assertFalse(viewModel.state.value.showWarning)
        assertTrue(preferences.isDuplicateFilesWarningAccepted())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `partial deletion counts unverified selections and repository skips`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                fun file(id: Long) =
                    ManagedFileItem(
                        id,
                        FileUri("content://media/$id"),
                        null,
                        "copy.pdf",
                        10L,
                        0L,
                        "application/pdf",
                        null,
                        ManagedFileType.Document,
                    )
                val original = file(1)
                val valid = listOf(2L, 3L).map { file(it).copy(duplicateHash = "hash", duplicatePeers = listOf(original)) }
                var submitted = emptyList<ManagedFileItem>()
                val repository =
                    object : FileRepository by UnusedFileRepository {
                        override suspend fun loadDuplicateFiles() = listOf(listOf(original) + valid + file(4))

                        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult {
                            submitted = items
                            return FileDeleteResult(
                                listOf(
                                    FileDeleteOutcome(items[0], DeleteStatus.Deleted),
                                    FileDeleteOutcome(items[1], DeleteStatus.Failed),
                                ),
                            )
                        }
                    }
                val model = DuplicateFilesManagerViewModel(repository, InMemorySettingsRepository(), dispatcher)
                model.dispatch(FileManagerAction.RefreshRequested)
                advanceUntilIdle()
                model.dispatch(FileManagerAction.SelectAllClicked)
                model.dispatch(FileManagerAction.DeleteConfirmed)
                advanceUntilIdle()

                assertEquals(valid, submitted)
                assertEquals(FileOperationPhase.Result, model.state.value.phase)
                assertEquals(2, model.state.value.skippedDeleteCount)
                assertEquals(10L, model.state.value.deletedBytes)
            } finally {
                Dispatchers.resetMain()
            }
        }

    private object UnusedFileRepository : FileRepository {
        override suspend fun loadImages(): List<ManagedFileItem> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileItem> = emptyList()

        override suspend fun loadAudios(): List<ManagedFileItem> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileItem> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileItem> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileItem> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItem> = emptyList()

        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> = emptyList()

        override suspend fun loadFilesInDirectories(
            relativeDirectories: List<String>,
            matchesMedia: (ManagedFileItem) -> Boolean,
        ): List<ManagedFileItem> = emptyList()

        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult = FileDeleteResult(emptyList())

        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int = 0
    }
}
