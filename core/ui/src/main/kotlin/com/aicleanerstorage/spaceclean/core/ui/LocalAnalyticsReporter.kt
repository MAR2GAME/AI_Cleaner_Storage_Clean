package com.aicleanerstorage.spaceclean.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter

val LocalAnalyticsReporter = staticCompositionLocalOf<AnalyticsReporter> {
    object : AnalyticsReporter {
        override fun report(event: AnalyticsEvent) = Unit
    }
}
