package com.aicleanerstorage.spaceclean.base.designsystem.component

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** 设计系统的通用强调色；不包含文件页等具体业务语义。 */
val CleanXAccentGreen = Color(0xFF5DD595)
val CleanXAccentLime = Color(0xFFD0FD91)
val CleanXTextColor = Color(0xFF282927)

val CleanXPrimaryGradient =
    Brush.horizontalGradient(
        listOf(CleanXAccentLime, CleanXAccentGreen),
    )
