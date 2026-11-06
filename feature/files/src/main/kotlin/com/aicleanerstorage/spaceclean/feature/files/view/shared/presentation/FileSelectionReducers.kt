package com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleId
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleIds
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleAllVisible
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.openDetailIndex


fun <T> toggleId(selectedIds: Set<T>, id: T): Set<T> =
    if (id in selectedIds) selectedIds - id else selectedIds + id

fun <T> toggleIds(selectedIds: Set<T>, ids: Set<T>): Set<T> =
    if (selectedIds.containsAll(ids)) selectedIds - ids else selectedIds + ids

fun <T> toggleAllVisible(selectedIds: Set<T>, visibleIds: Set<T>): Set<T> =
    toggleIds(selectedIds, visibleIds)

fun openDetailIndex(index: Int?): Int? =
    index?.takeIf { it >= 0 }
