package com.aicleanerstorage.spaceclean.app

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AppView(viewModel: AppViewModel) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.dispatch(AppAction.ClearMessage)
        }
    }
}

@Composable
fun AppEventHost(viewModel: AppViewModel) {
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { effect ->
            when (effect) {
                is AppEvent.OpenExternalIntent -> {
                    context.startActivity(effect.intent)
                }
            }
        }
    }
}
