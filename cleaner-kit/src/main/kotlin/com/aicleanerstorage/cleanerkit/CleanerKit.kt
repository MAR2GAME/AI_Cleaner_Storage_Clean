package com.aicleanerstorage.cleanerkit

import android.content.Context
import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.cleanerkit.clean.internal.CleanRepositoryImpl
import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.internal.FileRepositoryImpl

/** Reuse one instance for the lifetime of a scan session. */
class CleanerKit private constructor(
    context: Context,
    config: CleanerConfig,
) {
    val mediaDeletion = MediaDeletionAuthorization(context.contentResolver)
    val clean: CleanRepository by lazy { CleanRepositoryImpl(context, config.authorizeMediaDeletion, mediaDeletion) }
    val files: FileRepository by lazy { FileRepositoryImpl(context, config.authorizeMediaDeletion, mediaDeletion) }

    companion object {
        @JvmOverloads
        fun create(context: Context, config: CleanerConfig = CleanerConfig()): CleanerKit =
            CleanerKit(context.applicationContext, config)
    }
}
