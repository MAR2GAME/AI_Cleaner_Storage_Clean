package com.aicleanerstorage.spaceclean.app.navigation.owner

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.aicleanerstorage.spaceclean.app.navigation.*
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerMediaType

internal object FeatureOwnerRegistry {
    fun resolve(entry: NavBackStackEntry?, controller: NavHostController): FeatureOwner? {
        entry ?: return null
        return when {
            entry.destination.hierarchy.any { it.hasRoute<PhotosFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<PhotosFlow>(), FileManagerMediaType.Photos)
            entry.destination.hierarchy.any { it.hasRoute<SimilarPhotosFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<SimilarPhotosFlow>(), FileManagerMediaType.SimilarPhotos)
            entry.destination.hierarchy.any { it.hasRoute<ScreenshotsFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<ScreenshotsFlow>(), FileManagerMediaType.Screenshots)
            entry.destination.hierarchy.any { it.hasRoute<VideosFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<VideosFlow>(), FileManagerMediaType.Videos)
            entry.destination.hierarchy.any { it.hasRoute<AudiosFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<AudiosFlow>(), FileManagerMediaType.Audios)
            entry.destination.hierarchy.any { it.hasRoute<LargeFilesFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<LargeFilesFlow>(), FileManagerMediaType.LargeFiles)
            entry.destination.hierarchy.any { it.hasRoute<DocumentsFlow>() } ->
                FeatureOwner.FileManager(controller.getBackStackEntry<DocumentsFlow>(), FileManagerMediaType.Documents)
            entry.destination.hasRoute<AppDestination.PhotoPrivacyManager>() ->
                FeatureOwner.FileManager(entry, FileManagerMediaType.PhotoPrivacy)
            entry.destination.hasRoute<AppDestination.DuplicateFilesManager>() ->
                FeatureOwner.FileManager(entry, FileManagerMediaType.DuplicateFiles)
            entry.destination.hierarchy.any { it.hasRoute<AntiVirusFlow>() } ->
                FeatureOwner.AntiVirus(controller.getBackStackEntry<AntiVirusFlow>())
            entry.destination.hasRoute<AppDestination.JunkClean>() -> FeatureOwner.JunkClean(entry)
            else -> null
        }
    }
}
