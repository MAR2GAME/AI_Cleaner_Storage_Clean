package com.aicleanerstorage.spaceclean.data.local.files

import android.content.Context
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal object FileLocationMetadata {
    fun hasGpsLocation(context: Context, item: ManagedFileModel): Boolean =
        runCatching {
            val path = item.path
            if (!path.isNullOrBlank()) {
                ExifInterface(path).hasGpsLocation()
            } else {
                val itemUri = item.uri.toAndroidUri()
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.setRequireOriginal(itemUri) else itemUri
                context.contentResolver.openInputStream(uri)?.use { ExifInterface(it).hasGpsLocation() } ?: false
            }
        }.getOrDefault(false)

    private fun ExifInterface.hasGpsLocation(): Boolean =
        FloatArray(2).let(::getLatLong) || getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null

    private fun clearGpsAttributes(exif: ExifInterface) {
        listOf(
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ).forEach { exif.setAttribute(it, null) }
    }

    suspend fun removeLocationData(context: Context, items: List<ManagedFileModel>): Int =
        withContext(Dispatchers.IO) {
            var changed = 0
            items.forEach { item ->
                coroutineContext.ensureActive()
                val path = item.path
                if (!path.isNullOrBlank()) {
                    val removed =
                        runCatching {
                            val exif = ExifInterface(path)
                            clearGpsAttributes(exif)
                            exif.saveAttributes()
                            true
                        }.getOrDefault(false)
                    if (removed) changed++
                }
            }
            changed
        }

}
