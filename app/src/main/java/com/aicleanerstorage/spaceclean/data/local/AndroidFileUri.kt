package com.aicleanerstorage.spaceclean.data.local

import android.net.Uri
import com.aicleanerstorage.spaceclean.domain.model.FileUri

fun Uri.toFileUri(): FileUri = FileUri(toString())

fun FileUri.toAndroidUri(): Uri = Uri.parse(value)
