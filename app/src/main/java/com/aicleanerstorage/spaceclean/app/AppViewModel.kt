package com.aicleanerstorage.spaceclean.app

import android.content.Intent
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel

data class AppState(
    val message: String? = null,
)

sealed interface AppIntent {
    data class ShowMessage(val value: String) : AppIntent
    data object ClearMessage : AppIntent
    data class OpenExternalIntent(val intent: Intent) : AppIntent
}

sealed interface AppEffect {
    data class OpenExternalIntent(val intent: Intent) : AppEffect
}

class AppViewModel : MviViewModel<AppState, AppIntent, AppEffect>(
    initialState = AppState(),
) {
    override fun onIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.ShowMessage -> updateState { copy(message = intent.value) }
            AppIntent.ClearMessage -> updateState { copy(message = null) }
            is AppIntent.OpenExternalIntent -> emitEffect(AppEffect.OpenExternalIntent(intent.intent))
        }
    }
}
