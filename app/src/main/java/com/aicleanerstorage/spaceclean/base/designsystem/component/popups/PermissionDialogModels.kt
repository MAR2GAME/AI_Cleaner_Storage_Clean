package com.aicleanerstorage.spaceclean.base.designsystem.component.popups

import androidx.annotation.DrawableRes

/** 权限弹窗的一条要点：图标 + 文案。 */
data class PermissionHint(
    @param:DrawableRes val iconRes: Int,
    val text: String,
)

/**
 * 权限解释弹窗的展示数据。
 *
 * @property title 标题，弹窗统一加 ✨ 前缀。
 * @property description 用途说明；null 时不显示说明区。
 * @property confirmText 主按钮文案。
 * @property cancelText 次按钮文案；null 表示只显示主按钮。
 * @property hints 图标要点，按顺序展示。
 * @property heroImageRes 顶部插画；null 表示不显示。
 */
data class PermissionDialogCopy(
    val title: String,
    val description: String?,
    val confirmText: String,
    val cancelText: String?,
    val hints: List<PermissionHint> = emptyList(),
    @param:DrawableRes val heroImageRes: Int? = null,
)
