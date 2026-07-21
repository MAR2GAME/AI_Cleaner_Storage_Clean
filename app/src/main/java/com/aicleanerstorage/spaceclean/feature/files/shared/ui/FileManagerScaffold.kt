package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.common.permission.PermissionType

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureKey
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.NoResultsDialog
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.ui.rememberPermissionGranted
import com.aicleanerstorage.spaceclean.feature.files.shared.BaseFileManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.requestMediaStoreDeleteOrDeleteDirectly

@Composable
internal fun FileManagerScaffold(
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
