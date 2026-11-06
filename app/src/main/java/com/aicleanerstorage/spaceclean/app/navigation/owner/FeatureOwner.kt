package com.aicleanerstorage.spaceclean.app.navigation.owner

import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerMediaType

internal sealed interface FeatureOwner {
    val ownerEntry: NavBackStackEntry

    data class FileManager(
        override val ownerEntry: NavBackStackEntry,
        val mediaType: FileManagerMediaType,
    ) : FeatureOwner
    data class JunkClean(override val ownerEntry: NavBackStackEntry) : FeatureOwner
    data class AntiVirus(override val ownerEntry: NavBackStackEntry) : FeatureOwner
}
