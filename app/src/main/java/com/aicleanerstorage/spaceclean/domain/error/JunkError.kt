package com.aicleanerstorage.spaceclean.domain.error

/** Failures of the junk scan/clean flow. */
sealed interface JunkError {
    data object ScanFailed : JunkError

    data object CleanFailed : JunkError

    data object CleanFailedAfterAuthorization : JunkError

    data object NoData : JunkError
}
