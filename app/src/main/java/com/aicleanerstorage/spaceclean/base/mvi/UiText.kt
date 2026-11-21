package com.aicleanerstorage.spaceclean.base.mvi

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** User visible text; ViewModels never hold a Context. */
sealed interface UiText {
    data class Res(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    data class Raw(
        val value: String,
    ) : UiText
}

fun UiText.resolve(context: Context): String =
    when (this) {
        is UiText.Res -> context.getString(id, *args.toTypedArray())
        is UiText.Raw -> value
    }

/** Composition-safe variant: renders resources without touching [Context]. */
@Composable
fun UiText.asString(): String =
    when (this) {
        is UiText.Res -> stringResource(id, *args.toTypedArray())
        is UiText.Raw -> value
    }
