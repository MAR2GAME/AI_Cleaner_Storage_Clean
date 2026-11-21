package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.error.JunkError

internal fun JunkError.toUiText(): UiText =
    when (this) {
        JunkError.ScanFailed -> UiText.Res(R.string.scan_failed)
        JunkError.CleanFailed -> UiText.Res(R.string.result_clean_error)
        JunkError.CleanFailedAfterAuthorization -> UiText.Res(R.string.result_clean_error_after_authorization)
        JunkError.NoData -> UiText.Res(R.string.result_no_data)
    }
