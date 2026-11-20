package com.aicleanerstorage.spaceclean.base.designsystem.component

import com.aicleanerstorage.spaceclean.base.designsystem.component.AllToolFeatures
import com.aicleanerstorage.spaceclean.base.designsystem.component.handleToolFeatureBannerClick
import com.aicleanerstorage.spaceclean.base.designsystem.component.selectRecommendedToolFeatures

import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolFeatureRecommendationTest {
    @Test
    fun `same seed returns stable recommendations`() {
        val first = selectRecommendedToolFeatures(AllToolFeatures, seed = 17L)
        val second = selectRecommendedToolFeatures(AllToolFeatures, seed = 17L)

        assertEquals(first.map { it.destination }, second.map { it.destination })
    }

    @Test
    fun `different seeds can return different recommendations`() {
        val selections =
            (0L..20L)
                .map { seed ->
                    selectRecommendedToolFeatures(AllToolFeatures, seed = seed)
                        .map { it.destination }
                }
                .toSet()

        assertTrue(selections.size > 1)
    }

    @Test
    fun `excluded and duplicate destinations are omitted`() {
        val duplicatePool = AllToolFeatures + AllToolFeatures.first()
        val selected =
            selectRecommendedToolFeatures(
                features = duplicatePool,
                excludedDestinations = setOf(ToolNavigationTarget.JunkClean),
                seed = 9L,
                limit = 20,
            )

        assertFalse(selected.any { it.destination == ToolNavigationTarget.JunkClean })
        assertEquals(selected.size, selected.map { it.destination }.distinct().size)
        assertTrue(selected.size <= AllToolFeatures.size - 1)
    }

    @Test
    fun `selection returns at most two recommendations by default`() {
        val selected = selectRecommendedToolFeatures(AllToolFeatures, seed = 3L)

        assertEquals(2, selected.size)
    }

    @Test
    fun `tool feature click navigates to destination`() {
        val calls = mutableListOf<String>()

        handleToolFeatureBannerClick(
            destination = ToolNavigationTarget.DeviceInfo,
            onNavigateTool = { calls += "navigate:$it" },
        )

        assertEquals(listOf("navigate:${ToolNavigationTarget.DeviceInfo}"), calls)
    }
}
