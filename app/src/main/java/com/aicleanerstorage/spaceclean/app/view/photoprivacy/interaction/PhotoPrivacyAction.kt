package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 位置移除页的完整动作集合。 */
sealed interface PhotoPrivacyAction {
    data class NavigationRequested(
        val destination: AppDestination,
        val reset: Boolean = true,
    ) : PhotoPrivacyAction

    data object PermissionRejected : PhotoPrivacyAction

    data object BackClicked : PhotoPrivacyAction

    data object StopDismissed : PhotoPrivacyAction

    data object OperationCancellationRequested : PhotoPrivacyAction

    data object InitialLoadRequested : PhotoPrivacyAction

    data object RefreshRequested : PhotoPrivacyAction

    data class SelectionToggled(
        val id: Int,
    ) : PhotoPrivacyAction

    data object VisibleItemsToggled : PhotoPrivacyAction

    data class VisibleSelectionToggled(
        val ids: Set<Int>,
    ) : PhotoPrivacyAction

    data object DeleteClicked : PhotoPrivacyAction

    data object DeleteDismissed : PhotoPrivacyAction

    data object DeletePrepared : PhotoPrivacyAction

    data object LocationRemovalClicked : PhotoPrivacyAction

    data object LocationRemovalDismissed : PhotoPrivacyAction

    data object ErrorDismissed : PhotoPrivacyAction

    data object LocationRemovalConfirmed : PhotoPrivacyAction

    data object ContinueManagingClicked : PhotoPrivacyAction
}
