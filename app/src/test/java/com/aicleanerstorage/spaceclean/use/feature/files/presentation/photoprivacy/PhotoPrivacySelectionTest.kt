package com.aicleanerstorage.spaceclean.feature.files.photoprivacy

import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri
import com.aicleanerstorage.spaceclean.feature.files.shared.ManagedFileItem
import com.aicleanerstorage.spaceclean.feature.files.shared.ManagedFileType
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileDeleteResult
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileRepository
import java.util.Calendar
import java.util.TimeZone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoPrivacySelectionTest {
    @Test
    fun `groups seconds and milliseconds by descending local month`() {
        withDefaultTimeZone(TimeZone.getTimeZone("UTC")) {
            val mayFirst = timestampMillis(2026, Calendar.MAY, 1)
            val aprilFirst = timestampMillis(2026, Calendar.APRIL, 1)
            val items = mapPrivacyPhotos(
                listOf(
                    image(1, aprilFirst / 1000L),
                    image(2, mayFirst),
                    image(3, mayFirst / 1000L),
                )
            )

            val groups = buildPrivacyPhotoGroups(items)

            assertEquals(listOf("2026-05", "2026-04"), groups.map { it.title })
            assertEquals(listOf(2L, 3L), groups.first().items.map { it.file.id })
            assertEquals(listOf(1L), groups.last().items.map { it.file.id })
        }
    }

    @Test
    fun `global and group toggles keep selection states synchronized`() = runTest {
        val viewModel = viewModel(FakeFileRepository()) { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()

        val initial = viewModel.uiState.value
        assertTrue(initial.allSelected)
        assertEquals(initial.visibleIds, initial.selectedIds)

        viewModel.toggleVisibleItems()
        assertTrue(viewModel.uiState.value.selectedIds.isEmpty())
        assertFalse(viewModel.uiState.value.allSelected)

        val firstGroupIds = initial.groups.first().ids
        viewModel.toggleVisibleIds(firstGroupIds)
        assertEquals(firstGroupIds, viewModel.uiState.value.selectedIds)
        assertFalse(viewModel.uiState.value.allSelected)

        viewModel.toggleVisibleItems()
        assertTrue(viewModel.uiState.value.allSelected)
        assertEquals(initial.visibleIds, viewModel.uiState.value.selectedIds)
    }

    @Test
    fun `removing location data submits only selected photos and rebuilds groups`() = runTest {
        val repository = FakeFileRepository()
        val viewModel = viewModel(repository) { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()
        viewModel.toggleVisibleItems()
        viewModel.toggleSelection(1)
        viewModel.removeLocationData()
        advanceUntilIdle()

        assertEquals(listOf(1L), repository.removedIds)
        assertEquals(FileOperationPhase.CompleteAnimation, viewModel.uiState.value.phase)
        assertTrue(viewModel.uiState.value.items.none { it.file.id == 1L })
        assertTrue(viewModel.uiState.value.selectedIds.isEmpty())
    }

    private fun viewModel(
        repository: FileRepository,
        testLoader: ((suspend () -> Unit) -> Unit),
    ): PhotoPrivacyManagerViewModel =
        PhotoPrivacyManagerViewModel(
            repository = repository,
            testLoader = testLoader,
            scanDelayMillis = 0L,
            removeDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository : FileRepository {
        private var privacyImages = listOf(
            image(1, timestampMillis(2026, Calendar.MAY, 1) / 1000L),
            image(2, timestampMillis(2026, Calendar.MAY, 2) / 1000L),
            image(3, timestampMillis(2026, Calendar.APRIL, 1) / 1000L),
        )
        var removedIds: List<Long> = emptyList()
            private set

        override suspend fun loadImages(): List<ManagedFileItem> = emptyList()
        override suspend fun loadVideos(): List<ManagedFileItem> = emptyList()
        override suspend fun loadAudios(): List<ManagedFileItem> = emptyList()
        override suspend fun loadScreenshots(): List<ManagedFileItem> = emptyList()
        override suspend fun loadPrivacyImages(): List<ManagedFileItem> = privacyImages
        override suspend fun loadDocuments(): List<ManagedFileItem> = emptyList()
        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItem> = emptyList()
        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> = emptyList()
        override suspend fun loadWhatsAppFiles(): List<ManagedFileItem> = emptyList()
        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult = FileDeleteResult(0L, 0)
        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int {
            removedIds = items.map { it.id }
            privacyImages = privacyImages.filterNot { candidate -> items.any { it.id == candidate.id } }
            return items.size
        }
        override fun hasAllFilesAccess(): Boolean = false
    }

    private companion object {
        fun image(id: Long, modifiedSeconds: Long): ManagedFileItem =
            ManagedFileItem(
                id = id,
                uri = FileUri("content://media/external/images/media/$id"),
                path = null,
                name = "image-$id.jpg",
                sizeBytes = id * 1024L,
                modifiedSeconds = modifiedSeconds,
                mimeType = "image/jpeg",
                bucketName = "Camera",
                type = ManagedFileType.Image,
            )

        fun timestampMillis(year: Int, month: Int, day: Int): Long =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
                clear()
                set(year, month, day, 12, 0, 0)
                timeInMillis
            }

        fun withDefaultTimeZone(timeZone: TimeZone, block: () -> Unit) {
            val previous = TimeZone.getDefault()
            try {
                TimeZone.setDefault(timeZone)
                block()
            } finally {
                TimeZone.setDefault(previous)
            }
        }
    }
}
