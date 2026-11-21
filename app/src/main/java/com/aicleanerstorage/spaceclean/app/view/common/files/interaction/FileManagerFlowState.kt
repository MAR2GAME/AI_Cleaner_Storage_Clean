package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

data class FileManagerFlowState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
)
