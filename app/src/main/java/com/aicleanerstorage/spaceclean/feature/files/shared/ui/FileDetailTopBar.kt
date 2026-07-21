package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

internal fun fileDetailTitle(
    selectedCount: Int,
    totalCount: Int,
): String = "${selectedCount.coerceAtLeast(0)}/${totalCount.coerceAtLeast(0)}"
