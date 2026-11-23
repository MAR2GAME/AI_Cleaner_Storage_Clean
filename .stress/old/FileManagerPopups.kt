package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aicleanerstorage.spaceclean.R
import kotlinx.coroutines.delay

private val FileDialogTextColor = Color(0xFF282927)
private val FileDialogDismissColor = Color(0xFF999999)
private val FileDialogDeleteColor = Color(0xFFFF4F51)
private val FileDialogGradientStart = Color(0xFFDFFFDC)
private val FileDialogButtonStart = Color(0xFFD0FD91)
private val FileDialogButtonEnd = Color(0xFF5DD595)
private val FileDialogShape = RoundedCornerShape(12.dp)
private val FileDialogButtonShape = RoundedCornerShape(26.dp)

@Composable
internal fun DeleteConfirmDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    title: String? = null,
    message: String? = null,
    confirmText: String? = null,
) {
    val dialogTitle = title ?: stringResource(R.string.file_delete_permanently_title)
    val dialogMessage = message ?: stringResource(R.string.file_delete_permanently_message)
    val dialogConfirmText = confirmText ?: stringResource(R.string.file_delete)

    FileManagerDialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FileManagerDialogTitle(text = dialogTitle)
            Text(
                text = dialogMessage,
                color = FileDialogTextColor,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                letterSpacing = 0.54.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FileManagerDialogButtonRow {
            FileManagerOutlinedButton(
                text = stringResource(R.string.cancel),
                onClick = onCancel,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            FileManagerDangerButton(
                text = dialogConfirmText,
                onClick = onDelete,
                modifier = Modifier.weight(1f).fillMaxHeight(),
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

    FileManagerDialog(onDismissRequest = ::closeDialogThenBack) {
        FileManagerDialogTitle(text = stringResource(R.string.file_scan_completed_no_results))
        FileManagerGradientButton(
            text = stringResource(R.string.file_back_to_main_page),
            onClick = ::closeDialogThenBack,
            minHeight = 46.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FileManagerDialog(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
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
                            onClick = onDismissRequest,
                        ),
            )
            Column(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .widthIn(max = 343.dp)
                        .fillMaxWidth()
                        .clip(FileDialogShape)
                        .background(
                            Brush.verticalGradient(
                                colorStops =
                                    arrayOf(
                                        0f to FileDialogGradientStart,
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
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun FileManagerDialogTitle(text: String) {
    Text(
        text = text,
        color = FileDialogTextColor,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.72.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun FileManagerDialogButtonRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        content = content,
    )
}

@Composable
private fun FileManagerOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        shape = FileDialogButtonShape,
        border = BorderStroke(1.56.dp, FileDialogDismissColor),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = FileDialogDismissColor,
            ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = FileDialogDismissColor,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FileManagerGradientButton(
    text: String,
    onClick: () -> Unit,
    minHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .heightIn(min = minHeight)
                .background(
                    brush = Brush.horizontalGradient(listOf(FileDialogButtonStart, FileDialogButtonEnd)),
                    shape = FileDialogButtonShape,
                ),
        shape = FileDialogButtonShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = FileDialogTextColor,
            ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = FileDialogTextColor,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FileManagerDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        shape = FileDialogButtonShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = FileDialogDeleteColor,
                contentColor = Color.White,
            ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
