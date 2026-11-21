package com.aicleanerstorage.spaceclean.base.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.base.designsystem.theme.LocalAppThemeTokens

// ============================================================================
//   閻滄澘婀幍鈧張澶婃槀閿?閸﹀棜顫楅柈鎴掔矤 LocalAppThemeTokens 閸欐牭绱濇穱婵婄槈娑撳顣介崣顖氬瀼閿?
// ============================================================================

private val Dimens: com.aicleanerstorage.spaceclean.base.designsystem.theme.AppDimens
    @Composable @ReadOnlyComposable get() = LocalAppThemeTokens.current.dimens

private val Shapes: com.aicleanerstorage.spaceclean.base.designsystem.theme.AppShapes
    @Composable @ReadOnlyComposable get() = LocalAppThemeTokens.current.shapes

// ---------- Spacing ----------
val CleanXPagePadding: Dp @Composable @ReadOnlyComposable get() = Dimens.pagePadding
val CleanXCompactPadding: Dp @Composable @ReadOnlyComposable get() = Dimens.compactPadding
val CleanXHeaderTopPadding: Dp @Composable @ReadOnlyComposable get() = Dimens.headerTopPadding
val CleanXHeaderBottomPadding: Dp @Composable @ReadOnlyComposable get() = Dimens.headerBottomPadding
val CleanXButtonHorizontalPadding: Dp @Composable @ReadOnlyComposable get() = Dimens.buttonHorizontalPadding
val CleanXButtonVerticalPadding: Dp @Composable @ReadOnlyComposable get() = Dimens.buttonVerticalPadding

// ---------- Sizes ----------
val CleanXButtonHeight: Dp @Composable @ReadOnlyComposable get() = Dimens.buttonHeight
val CleanXCompactButtonHeight: Dp @Composable @ReadOnlyComposable get() = Dimens.compactButtonHeight
val CleanXIconButtonSize: Dp @Composable @ReadOnlyComposable get() = Dimens.iconButtonSize
val CleanXHeaderIconSize: Dp @Composable @ReadOnlyComposable get() = Dimens.headerIconSize

// ---------- Shapes ----------
val CleanXCardShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.card
val CleanXTileShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.tile
val CleanXPillShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.pill
val CleanXButtonShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.button

val VirusPanelShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.virusPanel
val VirusButtonShape: RoundedCornerShape @Composable @ReadOnlyComposable get() = Shapes.virusButton

val CleanXTextTitle = 20.sp
val CleanXTextBody = 16.sp
val CleanXTextCaption = 14.sp
val CleanXTextTiny = 12.sp
val CleanXLineTitle = 27.sp
val CleanXLineSubtitle = 22.sp
val CleanXLineBody = 20.sp
val CleanXLineCaption = 18.sp
