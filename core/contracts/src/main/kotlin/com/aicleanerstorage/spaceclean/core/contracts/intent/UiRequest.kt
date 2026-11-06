package com.aicleanerstorage.spaceclean.core.contracts.intent

sealed interface UiRequest {
    val requestId: String

    data class Permission(
        override val requestId: String,
        val permissions: List<String>,
    ) : UiRequest

    data class Confirm(
        override val requestId: String,
        val title: String,
        val message: String,
        val confirmText: String = "确定",
        val cancelText: String = "取消",
    ) : UiRequest

    data class LaunchActivity(
        override val requestId: String,
        val action: String,
        val payload: Map<String, String> = emptyMap(),
    ) : UiRequest

    data class ShowInterstitial(
        override val requestId: String,
        val placement: String,
    ) : UiRequest

    data class MediaStoreDelete(
        override val requestId: String,
        val uris: List<String>,
    ) : UiRequest
}

sealed interface UiResult {
    data object Granted : UiResult
    data object Confirmed : UiResult
    data object Denied : UiResult
    data object Cancelled : UiResult
    data object Failed : UiResult
    data class ActivityResult(val resultCode: Int) : UiResult
}

interface UiRequestPort {
    suspend fun request(request: UiRequest): UiResult
}
