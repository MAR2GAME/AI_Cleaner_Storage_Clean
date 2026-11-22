package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 相似照片页的完整动作集合。 */
sealed interface SimilarPhotosAction {
    data class NavigationRequested(
        val destination: AppDestination,
        val reset: Boolean = true,
    ) : SimilarPhotosAction

    data object PermissionRejected : SimilarPhotosAction

    data object BackClicked : SimilarPhotosAction

    data object DetailBackClicked : SimilarPhotosAction

    data object StopDismissed : SimilarPhotosAction

    data object OperationCancellationRequested : SimilarPhotosAction

    data object InitialLoadRequested : SimilarPhotosAction

    data object RefreshRequested : SimilarPhotosAction

    data class SelectionToggled(
        val id: Int,
    ) : SimilarPhotosAction

    data object VisibleItemsToggled : SimilarPhotosAction

    data class GroupToggled(
        val ids: Set<Int>,
    ) : SimilarPhotosAction

    data class GroupItemsToggled(
        val ids: Set<Int>,
    ) : SimilarPhotosAction

    data class DetailClicked(
        val index: Int,
    ) : SimilarPhotosAction

    data object DeleteClicked : SimilarPhotosAction

    data object DeleteDismissed : SimilarPhotosAction

    data object ErrorDismissed : SimilarPhotosAction

    data object DeleteConfirmed : SimilarPhotosAction

    data object ContinueManagingClicked : SimilarPhotosAction
}
