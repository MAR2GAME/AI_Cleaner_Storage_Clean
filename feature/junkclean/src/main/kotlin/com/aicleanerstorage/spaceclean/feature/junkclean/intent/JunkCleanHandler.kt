package com.aicleanerstorage.spaceclean.feature.junkclean.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkDeleteOutcome
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult

sealed interface JunkCleanResult {
    data class Scanned(val result: ScanResult) : JunkCleanResult
    data class Deleted(val outcomes: List<JunkDeleteOutcome>) : JunkCleanResult
}

class JunkCleanHandler(
    private val repository: CleanRepository,
) : IntentHandler<JunkCleanOperation, JunkCleanResult> {
    override suspend fun handle(request: IntentRequest<JunkCleanOperation>): JunkCleanResult =
        when (val operation = request.intent) {
            JunkCleanOperation.Scan -> JunkCleanResult.Scanned(repository.performFullScan())
            is JunkCleanOperation.Clean -> JunkCleanResult.Deleted(repository.deleteFileItems(operation.files))
        }
}
