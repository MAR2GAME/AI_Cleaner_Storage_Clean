package com.aicleanerstorage.spaceclean.app.view.common.tool

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Brush
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

data class ToolFeature(
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    val destination: AppDestination,
    val iconRes: Int,
    val gradient: Brush,
    @param:StringRes val actionLabelRes: Int,
)
