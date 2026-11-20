package com.aicleanerstorage.cleanerkit.clean.model

data class ScanProgress(
    val percent: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundCount: Int = 0,
    val foundSize: Long = 0,
) {
    companion object {
        val IDLE = ScanProgress(0f)

    }
}
