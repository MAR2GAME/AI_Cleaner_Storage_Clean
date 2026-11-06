package com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal data class PhotosManagerStateChanged(val value: PhotosManagerUiState) : FileManagerIntent
