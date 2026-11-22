package com.aicleanerstorage.spaceclean.domain.model.junk

/** 扫描进度；只用于展示，不参与业务判断。 */
data class JunkScanProgressModel(
    val percent: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundCount: Int = 0,
    val foundSize: Long = 0,
) {
    companion object {
        val IDLE = JunkScanProgressModel()
    }
}
