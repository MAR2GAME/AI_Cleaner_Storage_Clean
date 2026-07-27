package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.popups.CleanXSingleActionDialog
import kotlinx.coroutines.delay

private val DeleteDialogTextColor = Color(0xFF282927)
private val DeleteDialogDismissColor = Color(0xFF999999)
private val DeleteDialogConfirmColor = Color(0xFFFF4F51)
private val DeleteDialogGradientStart = Color(0xFFDFFFDC)

@Composable
internal fun DeleteConfirmDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    title: String? = null,
    message: String? = null,
    confirmText: String? = null
) {
    val dialogTitle = title ?: stringResource(R.string.file_delete_permanently_title)
    val dialogMessage = message ?: stringResource(R.string.file_delete_permanently_message)
    val dialogConfirmText = confirmText ?: stringResource(R.string.file_delete)
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCancel,
                        ),
            )
            DeleteConfirmDialogCard(
                title = dialogTitle,
                message = dialogMessage,
                dismissText = stringResource(R.string.cancel),
                confirmText = dialogConfirmText,
                onDismiss = onCancel,
                onConfirm = onDelete,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
internal fun NoResultsDialog(onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(true) }
    var pendingBack by remember { mutableStateOf(false) }

    fun closeDialogThenBack() {
        if (pendingBack) return
        showDialog = false
        pendingBack = true
    }

    LaunchedEffect(pendingBack) {
        if (!pendingBack) return@LaunchedEffect
        delay(200L)
        pendingBack = false
        onBack()
    }

    if (!showDialog) return

    CleanXSingleActionDialog(
        title = stringResource(R.string.file_scan_completed_no_results),
        actionText = stringResource(R.string.file_back_to_main_page),
        onDismissRequest = ::closeDialogThenBack,
        onAction = ::closeDialogThenBack,
    )
}

@Composable
private fun DeleteConfirmDialogCard(
    title: String,
    message: String,
    dismissText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 343.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0f to DeleteDialogGradientStart,
                                0.26381f to Color.White,
                                1f to Color.White,
                            ),
                    ),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .padding(start = 16.dp, end = 16.dp, top = 104.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                color = DeleteDialogTextColor,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.72.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = message,
                color = DeleteDialogTextColor,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                letterSpacing = 0.54.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f).fillMaxSize(),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.56.dp, DeleteDialogDismissColor),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = DeleteDialogDismissColor,
                    ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = dismissText,
                    color = DeleteDialogDismissColor,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f).fillMaxSize(),
                shape = RoundedCornerShape(26.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = DeleteDialogConfirmColor,
                        contentColor = Color.White,
                    ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = confirmText,
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
