package com.aicleanerstorage.spaceclean.data.local.files

import android.net.Uri

internal fun Uri.to(): String = toString()

internal fun String.toAndroidUri(): Uri = Uri.parse(this)
