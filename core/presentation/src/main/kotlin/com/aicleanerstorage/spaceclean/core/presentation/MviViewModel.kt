package com.aicleanerstorage.spaceclean.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job

abstract class MviViewModel<S : Any, I : Any, E : Any>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val effectChannel = Channel<E>(Channel.UNLIMITED)
    val effects: Flow<E> = effectChannel.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    fun dispatch(intent: I) {
        if (viewModelScope.isActive) onIntent(intent)
    }

    protected abstract fun onIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    protected fun emitEffect(effect: E) {
        val result = effectChannel.trySend(effect)
        if (!result.isClosed) check(result.isSuccess)
    }

    protected fun launchTask(block: suspend () -> Unit): Job =
        viewModelScope.launch {
            try {
                block()
            } catch (error: CancellationException) {
                throw error
            }
        }

    override fun onCleared() {
        effectChannel.cancel()
        super.onCleared()
    }
}
