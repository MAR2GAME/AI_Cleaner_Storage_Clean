package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.files.FileDeleteResult
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler

sealed interface FileManagerResult {
    data class Loaded(val files: List<ManagedFileItem>) : FileManagerResult
    data class Deleted(val result: FileDeleteResult) : FileManagerResult
}

class FileManagerHandler(
    private val repository: FileRepository,
) : IntentHandler<FileManagerOperation, FileManagerResult> {
    override suspend fun handle(request: IntentRequest<FileManagerOperation>): FileManagerResult =
        when (val operation = request.intent) {
            is FileManagerOperation.LoadFiles -> FileManagerResult.Loaded(load(operation.feature))
            is FileManagerOperation.DeleteFiles -> FileManagerResult.Deleted(repository.deleteFiles(operation.files))
        }

    private suspend fun load(feature: FeatureKey): List<ManagedFileItem> = when (feature) {
        FeatureKey.PHOTOS -> repository.loadImages()
        FeatureKey.SIMILAR_PHOTOS -> repository.loadImages()
        FeatureKey.PHOTO_PRIVACY -> repository.loadPrivacyImages()
        FeatureKey.SCREENSHOTS -> repository.loadScreenshots()
        FeatureKey.VIDEOS -> repository.loadVideos()
        FeatureKey.AUDIOS -> repository.loadAudios()
        FeatureKey.LARGE_FILES -> repository.loadLargeFiles()
        FeatureKey.DOCUMENTS -> repository.loadDocuments()
        FeatureKey.DUPLICATE_FILES -> repository.loadDuplicateFiles().flatten()
        else -> emptyList()
    }
}
