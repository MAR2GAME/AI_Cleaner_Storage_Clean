package com.aicleanerstorage.spaceclean.feature.junkclean.domain.model

data class ScanProgress(
    val percent: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundCount: Int = 0,
    val foundSize: Long = 0,
) {
    companion object {
        val IDLE = ScanProgress(0f)

        val COMPLETE_PREFIX = ScanProgress(99f)
    }
}
