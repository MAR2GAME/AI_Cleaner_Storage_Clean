package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXEmptyScanResult
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPillTabs
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXTabItem
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerTabDisplayItem

@Composable
internal fun FileManagerNoResultsContent(
    tabs: List<FileManagerTabDisplayItem> = emptyList(),
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (tabs.isNotEmpty()) {
            CleanXPillTabs(
                items = tabs.map { tab -> CleanXTabItem(localizedFileManagerTabTitle(tab.title)) },
                selectedIndex = selectedTabIndex,
                onSelected = onTabSelected,
            )
        }
        CleanXEmptyScanResult(
            message = stringResource(R.string.file_scan_completed_no_results),
            modifier = Modifier.weight(1f),
            topPadding = if (tabs.isEmpty()) 77.dp else 40.dp,
        )
    }
}
