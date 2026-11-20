package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.VirusBlue
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.VirusButtonShape

@Composable
internal fun VirusPrimaryActionBar(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = VirusButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = VirusBlue,
            contentColor = Color.White,
            disabledContainerColor = VirusBlue.copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.75f),
        ),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
