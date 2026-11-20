package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import com.aicleanerstorage.spaceclean.domain.model.FileDeleteResultModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    fun `partial deletion counts unverified selections and repository skips`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            fun file(id: Long) = ManagedFileItemModel(
                id, FileUri("content://media/$id"), null, "copy.pdf", 10L, 0L,
                "application/pdf", null, ManagedFileType.Document,
            )
            val original = file(1)
            val valid = listOf(2L, 3L).map { file(it).copy(duplicateHash = "hash", duplicatePeers = listOf(original)) }
            var submitted = emptyList<ManagedFileItemModel>()
            val repository = object : FileRepository by UnusedFileRepository {
                override suspend fun loadDuplicateFiles() = listOf(listOf(original) + valid + file(4))
                override suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel {
                    submitted = items
                    return FileDeleteResultModel(freedBytes = 10L, deletedCount = 1, skippedCount = 1)
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
        override suspend fun loadImages(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadVideos(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadAudios(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadScreenshots(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadPrivacyImages(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadDocuments(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItemModel>> = emptyList()
        override suspend fun loadWhatsAppFiles(): List<ManagedFileItemModel> = emptyList()
        override suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel = FileDeleteResultModel(0L, 0)
        override suspend fun removeLocationData(items: List<ManagedFileItemModel>): Int = 0
    }
}
