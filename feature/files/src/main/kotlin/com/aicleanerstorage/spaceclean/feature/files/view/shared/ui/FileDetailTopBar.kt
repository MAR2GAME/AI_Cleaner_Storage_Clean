package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

fun fileDetailTitle(
    selectedCount: Int,
    totalCount: Int,
): String = "${selectedCount.coerceAtLeast(0)}/${totalCount.coerceAtLeast(0)}"
