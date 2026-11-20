package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
internal fun WhatsAppScanResultBottomBar(
    selectedBytes: Long,
    onClean: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .stableNavigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CleanXPrimaryButton(
            text = stringResource(R.string.remove_size, compactSizeLabel(selectedBytes)),
            onClick = onClean,
            enabled = selectedBytes > 0L,
        )
    }
}
