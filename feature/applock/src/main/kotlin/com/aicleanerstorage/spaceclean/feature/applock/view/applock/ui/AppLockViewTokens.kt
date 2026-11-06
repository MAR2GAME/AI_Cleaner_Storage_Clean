package com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.theme.LocalAppThemeTokens

const val PIN_LENGTH = 4

private val c @Composable @ReadOnlyComposable get() = LocalAppThemeTokens.current.colors

val AppLockBackground: Color @Composable @ReadOnlyComposable get() = c.pageBackground
val AppLockCardColor: Color @Composable @ReadOnlyComposable get() = c.cardBackground
val AppLockNavy: Color @Composable @ReadOnlyComposable get() = c.textPrimary
val AppLockSecondaryText: Color @Composable @ReadOnlyComposable get() = c.textMuted
val AppLockPlaceholderText: Color @Composable @ReadOnlyComposable get() = c.textMuted
val AppLockDividerColor: Color @Composable @ReadOnlyComposable get() = c.divider
val AppLockCardRadius: Dp @Composable @ReadOnlyComposable get() = c.let { 10.dp }
val PinSelectedColor: Color @Composable @ReadOnlyComposable get() = c.textPrimary
val PinUnselectedBorderColor: Color @Composable @ReadOnlyComposable get() = Color(0xA68190A5)
val PinKeyBackground: Color @Composable @ReadOnlyComposable get() = Color(0x408190A5)
val PinErrorColor: Color @Composable @ReadOnlyComposable get() = c.pinError
