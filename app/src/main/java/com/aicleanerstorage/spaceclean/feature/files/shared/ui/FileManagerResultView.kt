package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.CommonResultCheckIcon
import com.aicleanerstorage.spaceclean.common.ui.components.CommonResultContent
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerMutedTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXPrimaryButton

@Composable
internal fun FileManagerResultView(
    amount: String,
    unit: String,
    caption: String,
    onNavigateTool: (AppDestination) -> Unit,
    onContinue: () -> Unit,
) {
    CommonResultContent(
        onNavigateTool = onNavigateTool,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonResultCheckIcon(size = 45.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(amount, color = FileManagerTextColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(unit, color = FileManagerTextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(caption, color = FileManagerMutedTextColor, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            CleanXPrimaryButton(
                text = stringResource(R.string.file_continue_managing),
                onClick = onContinue
            )
        }
    }
}
