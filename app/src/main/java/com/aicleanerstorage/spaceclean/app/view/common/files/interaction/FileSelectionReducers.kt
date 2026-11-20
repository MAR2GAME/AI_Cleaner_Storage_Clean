package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleAllVisible
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.openDetailIndex


fun <T> toggleId(selectedIds: Set<T>, id: T): Set<T> =
    if (id in selectedIds) selectedIds - id else selectedIds + id

fun <T> toggleIds(selectedIds: Set<T>, ids: Set<T>): Set<T> =
    if (selectedIds.containsAll(ids)) selectedIds - ids else selectedIds + ids

fun <T> toggleAllVisible(selectedIds: Set<T>, visibleIds: Set<T>): Set<T> =
    toggleIds(selectedIds, visibleIds)

fun openDetailIndex(index: Int?): Int? =
    index?.takeIf { it >= 0 }
