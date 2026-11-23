package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesAction
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.testsupport.InMemoryAppSettingsRepository
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
        val preferences = InMemoryAppSettingsRepository()
        val viewModel =
            DuplicateFilesManagerViewModel(
                repository = UnusedFileRepository,
                settingsRepository = preferences,
                ioDispatcher = Dispatchers.Unconfined,
            )

        assertTrue(viewModel.state.value.showWarning)

        viewModel.dispatch(DuplicateFilesAction.WarningConfirmed)

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
                    ManagedFileModel(
                        id = id,
                        uri = "content://media/$id",
                        path = null,
                        name = "copy.pdf",
                        sizeBytes = 10L,
                        modifiedSeconds = 0L,
                        mimeType = "application/pdf",
                        bucketName = null,
                        kind = FileKind.Document,
                    )
                val original = file(1)
                val valid = listOf(2L, 3L).map { file(it) }
                var submitted = emptyList<ManagedFileModel>()
                val repository =
                    object : ManagedFileRepository by UnusedFileRepository {
                        override suspend fun loadDuplicateGroups() =
                            listOf(DuplicateFileGroupModel("hash", listOf(original) + valid + file(4)))

                        override suspend fun deleteDuplicates(
                            selections: List<DuplicateFileSelectionModel>,
                        ): Result<FileDeleteSummaryModel> {
                            submitted = selections.map { it.file }
                            return Result.success(
                                FileDeleteSummaryModel(deletedCount = 1, freedBytes = 10L, skippedCount = 1),
                            )
                        }
                    }
                val model = DuplicateFilesManagerViewModel(repository, InMemoryAppSettingsRepository(), dispatcher)
                model.dispatch(DuplicateFilesAction.RefreshRequested)
                advanceUntilIdle()
                model.dispatch(DuplicateFilesAction.SelectAllClicked)
                model.dispatch(DuplicateFilesAction.DeleteConfirmed)
                advanceUntilIdle()

                assertEquals(valid + file(4), submitted)
                assertEquals(FileOperationPhase.Browsing, model.state.value.phase)
                assertEquals(1, model.state.value.skippedDeleteCount)
                assertEquals(10L, model.state.value.deletedBytes)
            } finally {
                Dispatchers.resetMain()
            }
        }

    private object UnusedFileRepository : ManagedFileRepository {
        override suspend fun loadImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileModel> = emptyList()

        override suspend fun loadAudios(): List<ManagedFileModel> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileModel> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileModel> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileModel> = emptyList()

        override suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel> = emptyList()

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
            Result.success(FileDeleteSummaryModel(deletedCount = 0, freedBytes = 0L))

        override suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel> =
            Result.failure(UnsupportedOperationException())

        override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> = Result.success(0)
    }
}
