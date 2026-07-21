package com.aicleanerstorage.spaceclean.feature.onboarding

interface OnboardingPreferences {
    fun isScanCompleted(): Boolean

    fun markScanCompleted()
}
