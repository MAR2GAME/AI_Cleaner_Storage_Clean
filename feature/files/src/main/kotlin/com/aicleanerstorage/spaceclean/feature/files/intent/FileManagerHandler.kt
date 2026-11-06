package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.files.FileDeleteResult
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler

sealed interface FileManagerResult {
    data class Loaded(
        val mediaType: FileManagerMediaType,
        val files: List<ManagedFileItem>,
        val groups: List<List<ManagedFileItem>> = emptyList(),
    ) : FileManagerResult
    data class Deleted(val result: FileDeleteResult) : FileManagerResult
}

class FileManagerHandler(
    private val repository: FileRepository,
) : IntentHandler<FileManagerOperation, FileManagerResult> {
    override suspend fun handle(request: IntentRequest<FileManagerOperation>): FileManagerResult =
        when (val operation = request.intent) {
            is FileManagerOperation.Load -> load(operation.mediaType)
            is FileManagerOperation.Delete -> FileManagerResult.Deleted(repository.deleteFiles(operation.files))
        }

    private suspend fun load(mediaType: FileManagerMediaType): FileManagerResult.Loaded = when (mediaType) {
        FileManagerMediaType.Photos -> loaded(mediaType, repository.loadImages())
        FileManagerMediaType.SimilarPhotos -> loaded(mediaType, repository.loadImages())
        FileManagerMediaType.PhotoPrivacy -> loaded(mediaType, repository.loadPrivacyImages())
        FileManagerMediaType.Screenshots -> loaded(mediaType, repository.loadScreenshots())
        FileManagerMediaType.Videos -> loaded(mediaType, repository.loadVideos())
        FileManagerMediaType.Audios -> loaded(mediaType, repository.loadAudios())
        FileManagerMediaType.LargeFiles -> loaded(mediaType, repository.loadLargeFiles())
        FileManagerMediaType.Documents -> loaded(mediaType, repository.loadDocuments())
        FileManagerMediaType.DuplicateFiles -> {
            val groups = repository.loadDuplicateFiles()
            FileManagerResult.Loaded(mediaType, groups.flatten(), groups)
        }
    }

    private fun loaded(mediaType: FileManagerMediaType, files: List<ManagedFileItem>) =
        FileManagerResult.Loaded(mediaType, files)
}
