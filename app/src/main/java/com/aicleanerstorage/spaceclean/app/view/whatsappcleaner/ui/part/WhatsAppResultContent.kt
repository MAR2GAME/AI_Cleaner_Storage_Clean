package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXMutedText
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXText
import com.aicleanerstorage.spaceclean.base.designsystem.component.CommonResultCheckIcon
import com.aicleanerstorage.spaceclean.base.designsystem.component.CommonResultContent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

@Composable
internal fun WhatsAppResultContent(
    uiState: WhatsAppCleanerUiState,
    onNavigateTool: (AppDestination) -> Unit,
) {
    val sizeLabel = splitSizeLabel(uiState.deletedBytes)

    CommonResultContent(
        onNavigateTool = onNavigateTool,
        modifier = Modifier.fillMaxWidth(),
        excludedToolDestinations = setOf(AppDestination.WhatsAppCleaner),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                CommonResultCheckIcon(size = 45.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sizeLabel.main, color = CleanXText, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(sizeLabel.unit, color = CleanXText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.removed_files_size, uiState.deletedCount, FormatUtil.formatSize(uiState.deletedBytes)),
                color = CleanXMutedText,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
