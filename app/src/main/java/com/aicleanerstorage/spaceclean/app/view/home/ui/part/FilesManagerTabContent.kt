package com.aicleanerstorage.spaceclean.app.view.home.ui.part

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.home.interaction.FileManagerTargets
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
internal fun FilesManagerTabContent(onFeatureClick: (AppDestination) -> Unit = {}) {
    val categories = remember { FileManagerTargets }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HomeFeatureGridPanel(
                title = stringResource(R.string.home_file_manager_title),
                features = categories,
                iconSize = { it.homeManagerIconSize() },
                onFeatureClick = onFeatureClick,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun AppDestination.homeManagerIconSize(): DpSize =
    when (this) {
        AppDestination.PhotosManager -> DpSize(39.dp, 41.dp)
        AppDestination.SimilarPhotosManager -> DpSize(39.dp, 44.dp)
        AppDestination.PhotoPrivacyManager -> DpSize(44.dp, 44.dp)
        AppDestination.ScreenshotsManager -> DpSize(35.dp, 35.dp)
        AppDestination.VideosManager -> DpSize(40.dp, 30.dp)
        AppDestination.AudiosManager -> DpSize(40.dp, 39.dp)
        AppDestination.LargeFilesManager -> DpSize(40.dp, 41.dp)
        AppDestination.DuplicateFilesManager -> DpSize(44.dp, 44.dp)
        AppDestination.DocumentsManager -> DpSize(44.dp, 38.dp)
        else -> DpSize(40.dp, 40.dp)
    }
