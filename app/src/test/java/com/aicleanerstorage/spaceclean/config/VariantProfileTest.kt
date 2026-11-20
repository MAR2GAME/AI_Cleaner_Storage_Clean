package com.aicleanerstorage.spaceclean.config

import com.aicleanerstorage.spaceclean.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class VariantProfileTest {
    @Test
    fun productIdentityIsGeneratedIntoBuildConfig() {
        assertEquals("ai_cleaner_storage_clean", BuildConfig.PRODUCT_PROFILE_KEY)
        assertEquals("storage_clean", BuildConfig.PRODUCT_THEME_KEY)
        assertEquals("https://sites.google.com/view/aicleaner-storageclean/home", BuildConfig.TERMS_OF_SERVICE_URL)
        assertEquals("https://sites.google.com/view/aicleanerstorageclean/home", BuildConfig.PRIVACY_POLICY_URL)
    }
}
