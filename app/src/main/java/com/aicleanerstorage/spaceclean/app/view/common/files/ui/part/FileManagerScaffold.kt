package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage

@Composable
fun FileManagerScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable (androidx.compose.foundation.layout.RowScope.() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    CleanXScaffoldPage(
        title = title,
        scrollEnabled = false,
        contentPadding = PaddingValues(0.dp),
        backgroundBrush = FileManagerPageBrush,
        onBack = onBack,
        actions = actions,
        bottomBar = bottomBar,
    ) {
        content()
    }
}
