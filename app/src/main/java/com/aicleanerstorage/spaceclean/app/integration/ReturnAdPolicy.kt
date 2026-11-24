package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 返回广告只用于“离开功能回首页”，功能内部返回一律不弹。 */
internal object ReturnAdPolicy {
    fun shouldShowReturnAd(
        source: AppDestination?,
        backTargetIsMain: Boolean,
    ): Boolean = source != null && backTargetIsMain
}
