package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun AppLockCheckContent(
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(if (checked) R.drawable.ic_switch_open else R.drawable.ic_switch_close),
        contentDescription = null,
        modifier = modifier.size(24.dp)
    )
}
