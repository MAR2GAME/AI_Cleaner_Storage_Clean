package com.aicleanerstorage.spaceclean.feature.home.ui

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
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey

@Composable
fun FilesManagerTabContent(onFeatureClick: (FeatureKey) -> Unit = {}) {
    val categories = remember { FileManagerFeatureKeys.filter { AppDestination.forFeature(it) != null } }

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

private fun FeatureKey.homeManagerIconSize(): DpSize =
    when (this) {
        FeatureKey.PHOTOS -> DpSize(39.dp, 41.dp)
        FeatureKey.SIMILAR_PHOTOS -> DpSize(39.dp, 44.dp)
        FeatureKey.PHOTO_PRIVACY -> DpSize(44.dp, 44.dp)
        FeatureKey.SCREENSHOTS -> DpSize(35.dp, 35.dp)
        FeatureKey.VIDEOS -> DpSize(40.dp, 30.dp)
        FeatureKey.AUDIOS -> DpSize(40.dp, 39.dp)
        FeatureKey.LARGE_FILES -> DpSize(40.dp, 41.dp)
        FeatureKey.DUPLICATE_FILES -> DpSize(44.dp, 44.dp)
        FeatureKey.DOCUMENTS -> DpSize(44.dp, 38.dp)
        else -> DpSize(40.dp, 40.dp)
    }
