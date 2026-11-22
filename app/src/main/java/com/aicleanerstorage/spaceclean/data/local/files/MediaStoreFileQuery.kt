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
        return runCatching {
            resolver
                .query(collection, FILE_PROJECTION, null, null, "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC")
                ?.use { cursor -> readFileRows(cursor, collection, coroutineContext) }
                ?: throw java.io.IOException("MediaStore returned no cursor")
        }.getOrElse {
            coroutineContext.ensureActive()
            throw it
        }
    }

    private fun readFileRows(
        cursor: Cursor,
        collection: Uri,
        coroutineContext: CoroutineContext,
    ): List<ManagedFileModel> {
        val columns = cursor.fileQueryColumns()
        if (columns.id < 0 || columns.name < 0) return emptyList()
        return buildList {
            while (cursor.moveToNext()) {
                coroutineContext.ensureActive()
                val path = cursor.stringOrNull(columns.path)
                val name = cursor.stringOrNull(columns.name) ?: "Unknown"
                if (FileClassificationRule.shouldSkipPath(path, name)) continue
                val id = cursor.longOrZero(columns.id)
                add(fileModel(cursor, columns, collection, id, path, name))
            }
        }
    }

    private fun fileModel(
        cursor: Cursor,
        columns: FileQueryColumns,
        collection: Uri,
        id: Long,
        path: String?,
        name: String,
    ): ManagedFileModel =
        ManagedFileModel(
            id = id,
            uri = ContentUris.withAppendedId(collection, id).to(),
            path = path,
            name = name,
            sizeBytes = cursor.longOrZero(columns.size),
            modifiedSeconds = cursor.longOrZero(columns.modified),
            mimeType = cursor.stringOrNull(columns.mime),
            bucketName = path?.substringBeforeLast('/'),
            kind =
                when (cursor.intOrZero(columns.mediaType)) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> FileKind.Image
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> FileKind.Video
                    MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> FileKind.Audio
                    else -> FileKind.Document
                },
        )

    private fun Cursor.fileQueryColumns(): FileQueryColumns =
        FileQueryColumns(
            id = getColumnIndex(MediaStore.Files.FileColumns._ID),
            name = getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME),
            size = getColumnIndex(MediaStore.Files.FileColumns.SIZE),
            modified = getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED),
            mime = getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE),
            path = getColumnIndex(MediaStore.Files.FileColumns.DATA),
            mediaType = getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE),
        )

    private data class FileQueryColumns(
        val id: Int,
        val name: Int,
        val size: Int,
        val modified: Int,
        val mime: Int,
        val path: Int,
        val mediaType: Int,
    )

    private val FILE_PROJECTION =
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )

    private fun Cursor.stringOrNull(index: Int): String? = if (index >= 0 && !isNull(index)) getString(index) else null
    private fun Cursor.longOrZero(index: Int): Long = if (index >= 0 && !isNull(index)) getLong(index) else 0L
    private fun Cursor.intOrZero(index: Int): Int = if (index >= 0 && !isNull(index)) getInt(index) else 0
}

