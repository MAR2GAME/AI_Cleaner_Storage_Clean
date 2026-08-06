package com.aicleanerstorage.spaceclean.common.ads

import android.app.Activity

interface AdPrivacyGateway {
    fun isPrivacyOptionsRequired(): Boolean
    fun showPrivacyOptions(activity: Activity)
}
