package com.aicleanerstorage.cleanerkit.files.internal

import android.net.Uri
import com.aicleanerstorage.cleanerkit.files.model.FileUri

internal fun Uri.toFileUri(): FileUri = FileUri(toString())

internal fun FileUri.toAndroidUri(): Uri = Uri.parse(value)
