package com.aicleanerstorage.spaceclean.app

import android.content.Intent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AppViewModelTest {
    @Test
    fun `message is shown and cleared`() {
        val viewModel = AppViewModel()

        viewModel.dispatch(AppIntent.ShowMessage("message"))
        assertEquals("message", viewModel.state.value.message)

        viewModel.dispatch(AppIntent.ClearMessage)
        assertEquals(null, viewModel.state.value.message)
    }

    @Test
    fun `external intent effect is consumable`() = runTest {
        val viewModel = AppViewModel()
        val intent = Intent("test.action")

        viewModel.dispatch(AppIntent.OpenExternalIntent(intent))

        val effect = viewModel.effects.first()
        assertSame(intent, (effect as AppEffect.OpenExternalIntent).intent)
    }
}
