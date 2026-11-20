package com.aicleanerstorage.spaceclean.base.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object ExternalLinkUtil {
    fun open(context: Context, url: String): Boolean =
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            true
        }.getOrDefault(false)
}
