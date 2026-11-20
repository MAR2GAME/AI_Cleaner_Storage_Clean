package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import com.aicleanerstorage.spaceclean.R
import java.io.File

class PackageRemovedReceiver(
    private val onPackageRemoved: (String) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return
        val packageName = intent.data?.schemeSpecificPart?.takeIf { it.isNotBlank() } ?: return
        onPackageRemoved(packageName)
    }
}

fun openDeveloperSettings(
    context: Context,
) {
    try {
        Toast.makeText(context, context.getString(R.string.disable_usb), Toast.LENGTH_LONG).show()
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: ActivityNotFoundException) {
    } catch (_: Exception) {
    }
}

fun openAppSettings(
    context: Context,
    packageName: String?,
) {
    val targetPackageName = packageName?.takeIf { it.isNotBlank() } ?: return
    try {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:$targetPackageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    } catch (_: Exception) {
    }
}

fun File.safeDelete(context: Context): Boolean {
    return try {
        if (!exists()) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return delete()

        val uri = getMediaStoreUri(context, this) ?: return false
        val deletedRows = context.contentResolver.delete(uri, null, null)
        if (deletedRows > 0) {
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            true
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }
}

private fun getMediaStoreUri(context: Context, file: File): Uri? {
    val fileName = file.name
    val mimeType = context.contentResolver.getType(Uri.fromFile(file)) ?: "application/octet-stream"
    val collectionUri = when {
        mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        mimeType.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        mimeType.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }

    val projection = arrayOf(MediaStore.MediaColumns._ID)
    val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
    } else {
        "${MediaStore.MediaColumns.DATA} = ?"
    }
    val selectionArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val relativePath = file.parentFile?.absolutePath
            ?.replace("/storage/emulated/0/", "")
            ?.let { if (it.endsWith("/")) it else "$it/" } ?: return null
        arrayOf(fileName, relativePath)
    } else {
        arrayOf(file.absolutePath)
    }

    context.contentResolver.query(collectionUri, projection, selection, selectionArgs, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            return ContentUris.withAppendedId(collectionUri, id)
        }
    }
    return null
}
