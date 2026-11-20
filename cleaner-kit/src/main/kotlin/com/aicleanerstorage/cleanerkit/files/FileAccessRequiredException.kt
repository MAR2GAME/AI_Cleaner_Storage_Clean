package com.aicleanerstorage.cleanerkit.files

/** The host can request this permission or present the relevant settings screen. */
class FileAccessRequiredException(val permission: String) : SecurityException("Storage access required: " + permission)
