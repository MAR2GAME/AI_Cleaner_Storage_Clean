package com.aicleanerstorage.spaceclean.data.local.files

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ensureActive

internal object MediaStoreFileQuery {
    fun queryMedia(
        resolver: ContentResolver,
        collection: Uri,
        type: FileKind,
        coroutineContext: CoroutineContext,
    ): List<ManagedFileModel> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        )
        return runCatching {
            resolver.query(collection, projection, null, null, "${MediaStore.MediaColumns.DATE_MODIFIED} DESC")
                ?.use { cursor ->
                    val idCol = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                    val nameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    val sizeCol = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                    val modCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                    val mimeCol = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                    val pathCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val bucketCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                    if (idCol < 0 || nameCol < 0) return@use emptyList<ManagedFileModel>()
                    buildList<ManagedFileModel> {
                        while (cursor.moveToNext()) {
                            coroutineContext.ensureActive()
                            val path = cursor.stringOrNull(pathCol)
                            val name = cursor.stringOrNull(nameCol) ?: "Unknown"
                            if (FileClassificationRule.shouldSkipPath(path, name)) continue
                            val id = cursor.longOrZero(idCol)
                            add(
                                ManagedFileModel(
                                    id = id,
                                    uri = ContentUris.withAppendedId(collection, id).to(),
                                    path = path,
                                    name = name,
                                    sizeBytes = cursor.longOrZero(sizeCol),
                                    modifiedSeconds = cursor.longOrZero(modCol),
                                    mimeType = cursor.stringOrNull(mimeCol),
                                    bucketName = cursor.stringOrNull(bucketCol),
                                    kind = type,
                                ),
                            )
                        }
                    }
                } ?: throw java.io.IOException("MediaStore returned no cursor")
        }.getOrElse {
            coroutineContext.ensureActive()
            throw it
        }
    }

    fun queryFiles(resolver: ContentResolver, coroutineContext: CoroutineContext): List<ManagedFileModel> {
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )
        return runCatching {
            resolver.query(collection, projection, null, null, "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC")
                ?.use { cursor ->
                    val idCol = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                    val nameCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                    val modCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
                    val mimeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
                    val pathCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val mediaTypeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    if (idCol < 0 || nameCol < 0) return@use emptyList<ManagedFileModel>()
                    buildList<ManagedFileModel> {
                        while (cursor.moveToNext()) {
                            coroutineContext.ensureActive()
                            val path = cursor.stringOrNull(pathCol)
                            val name = cursor.stringOrNull(nameCol) ?: "Unknown"
                            if (FileClassificationRule.shouldSkipPath(path, name)) continue
                            val id = cursor.longOrZero(idCol)
                            add(
                                ManagedFileModel(
                                    id = id,
                                    uri = ContentUris.withAppendedId(collection, id).to(),
                                    path = path,
                                    name = name,
                                    sizeBytes = cursor.longOrZero(sizeCol),
                                    modifiedSeconds = cursor.longOrZero(modCol),
                                    mimeType = cursor.stringOrNull(mimeCol),
                                    bucketName = path?.substringBeforeLast('/'),
                                    kind = when (cursor.intOrZero(mediaTypeCol)) {
                                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> FileKind.Image
                                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> FileKind.Video
                                        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> FileKind.Audio
                                        else -> FileKind.Document
                                    },
                                ),
                            )
                        }
                    }
                } ?: throw java.io.IOException("MediaStore returned no cursor")
        }.getOrElse {
            coroutineContext.ensureActive()
            throw it
        }
    }

    private fun Cursor.stringOrNull(index: Int): String? = if (index >= 0 && !isNull(index)) getString(index) else null
    private fun Cursor.longOrZero(index: Int): Long = if (index >= 0 && !isNull(index)) getLong(index) else 0L
    private fun Cursor.intOrZero(index: Int): Int = if (index >= 0 && !isNull(index)) getInt(index) else 0
}
