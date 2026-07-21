package com.aicleanerstorage.spaceclean.feature.files.shared.data.source

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import android.net.Uri
import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri

internal fun Uri.toFileUri(): FileUri = FileUri(toString())

internal fun FileUri.toAndroidUri(): Uri = Uri.parse(value)
