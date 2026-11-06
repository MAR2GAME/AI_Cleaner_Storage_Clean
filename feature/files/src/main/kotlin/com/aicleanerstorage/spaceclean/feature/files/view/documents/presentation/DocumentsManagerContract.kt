package com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal data class DocumentsManagerStateChanged(val value: DocumentsManagerUiState) : FileManagerIntent
