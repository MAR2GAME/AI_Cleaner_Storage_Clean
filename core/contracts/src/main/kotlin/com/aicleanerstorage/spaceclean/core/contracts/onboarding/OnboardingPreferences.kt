package com.aicleanerstorage.spaceclean.core.contracts.onboarding

interface OnboardingPreferences {
    fun isScanCompleted(): Boolean

    fun markScanCompleted()
}
