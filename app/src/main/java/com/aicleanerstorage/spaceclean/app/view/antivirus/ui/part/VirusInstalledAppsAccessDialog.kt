package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun VirusInstalledAppsAccessDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    VirusDialogPanel(
        onDismiss = onDismiss,
        surfaceColor = Color.White,
    ) {
        Text(
            text = stringResource(R.string.permission_title_required),
            color = VirusInstalledAppsTitle,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.03.em,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.permission_installed_apps_desc),
            color = VirusInstalledAppsBody,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.03.em,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        VirusInstalledAppsFilledButton(stringResource(R.string.manage_permission), onOpenSettings)
        Spacer(modifier = Modifier.height(16.dp))
        VirusInstalledAppsOutlinedButton(stringResource(R.string.not_now), onDismiss)
    }
}

private val VirusInstalledAppsPrimary = Color(0xFF22A9E8)

private val VirusInstalledAppsTitle = Color(0xFF1D2959)

private val VirusInstalledAppsBody = Color(0xA61D2959)

@Composable
private fun VirusInstalledAppsFilledButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = virusDialogButtonShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = VirusInstalledAppsPrimary,
                contentColor = Color.White,
            ),
    ) {
        VirusInstalledAppsButtonText(text, Color.White)
    }
}

@Composable
private fun VirusInstalledAppsOutlinedButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = virusDialogButtonShape,
        border = BorderStroke(1.35.dp, VirusInstalledAppsPrimary),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = VirusInstalledAppsPrimary,
            ),
    ) {
        VirusInstalledAppsButtonText(text, VirusInstalledAppsPrimary)
    }
}

@Composable
private fun VirusInstalledAppsButtonText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        color = color,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}
