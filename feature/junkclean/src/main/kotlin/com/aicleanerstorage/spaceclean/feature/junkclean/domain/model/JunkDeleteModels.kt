package com.aicleanerstorage.spaceclean.feature.junkclean.domain.model

data class JunkDeleteOutcome(
    val junkFile: JunkFile,
    val deleted: Boolean,
    val freedBytes: Long,
    val authorizationUri: String? = null,
)

data class JunkAuthorizedDeleteResult(
    val cleanedFiles: List<JunkFile>,
    val failedFiles: List<JunkFile>,
    val freedBytes: Long,
)
