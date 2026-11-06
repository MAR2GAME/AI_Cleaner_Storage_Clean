package com.aicleanerstorage.spaceclean.feature.junkclean.intent

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile

sealed interface JunkCleanOperation {
    data object Scan : JunkCleanOperation
    data class Clean(val files: List<JunkFile>) : JunkCleanOperation
}
