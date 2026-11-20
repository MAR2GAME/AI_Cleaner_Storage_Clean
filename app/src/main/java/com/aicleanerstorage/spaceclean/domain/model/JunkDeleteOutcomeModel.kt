package com.aicleanerstorage.spaceclean.domain.model

data class JunkDeleteOutcomeModel(
    val junkFile: JunkFileModel,
    val deleted: Boolean,
    val freedBytes: Long,
    val authorizationUri: String? = null,
)
