package com.aicleanerstorage.spaceclean.app

import android.content.Intent
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel

data class AppState(
    val message: String? = null,
)

sealed interface AppAction {
    data class ShowMessage(val value: String) : AppAction
    data object ClearMessage : AppAction
    data class OpenExternalIntent(val intent: Intent) : AppAction
}

sealed interface AppEvent {
    data class OpenExternalIntent(val intent: Intent) : AppEvent
}

class AppViewModel : MviViewModel<AppState, AppAction, AppEvent>(
    initialState = AppState(),
) {
    override fun onAction(intent: AppAction) {
        when (intent) {
            is AppAction.ShowMessage -> setState { copy(message = intent.value) }
            AppAction.ClearMessage -> setState { copy(message = null) }
            is AppAction.OpenExternalIntent -> sendEvent(AppEvent.OpenExternalIntent(intent.intent))
        }
    }
}
