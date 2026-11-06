package com.aicleanerstorage.spaceclean.feature.files.data.shared

import android.net.Uri
import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri

fun Uri.toFileUri(): FileUri = FileUri(toString())

fun FileUri.toAndroidUri(): Uri = Uri.parse(value)
