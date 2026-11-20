package com.aicleanerstorage.spaceclean.domain.model

data class ScanProgressModel(
    val percent: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundCount: Int = 0,
    val foundSize: Long = 0,
) {
    companion object {
        val IDLE = ScanProgressModel(0f)

    }
}
