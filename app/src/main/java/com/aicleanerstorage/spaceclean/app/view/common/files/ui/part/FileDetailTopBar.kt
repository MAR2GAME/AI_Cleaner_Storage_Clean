package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

fun fileDetailTitle(
    selectedCount: Int,
    totalCount: Int,
): String = "${selectedCount.coerceAtLeast(0)}/${totalCount.coerceAtLeast(0)}"
