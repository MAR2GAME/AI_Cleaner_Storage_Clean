package com.aicleanerstorage.spaceclean.base.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 统一 MVI 基类：
 * - [State] 由 StateFlow 暴露，只有一个来源；
 * - [Action] 是页面唯一输入（用户动作 + 异步结果）；
 * - [Event] 是一次性副作用（导航、Snackbar、权限请求），由 Route 收集。
 */
abstract class MviViewModel<State : Any, Action : Any, Event : Any>(
    initialState: State,
) : ViewModel() {
    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    val event: Flow<Event> = eventChannel.receiveAsFlow()

    protected val currentState: State
        get() = mutableState.value

    fun dispatch(action: Action) {
        if (viewModelScope.isActive) onAction(action)
    }

    protected abstract fun onAction(action: Action)

    protected fun setState(reducer: State.() -> State) {
        mutableState.update(reducer)
    }

    protected fun changeState(transform: (State) -> State) {
        if (viewModelScope.isActive) setState(transform)
    }

    protected fun sendEvent(event: Event) {
        val result = eventChannel.trySend(event)
        if (!result.isClosed) check(result.isSuccess)
    }

    protected fun launch(block: suspend () -> Unit): Job =
        viewModelScope.launch {
            try {
                block()
            } catch (error: CancellationException) {
                throw error
            }
        }

    override fun onCleared() {
        eventChannel.cancel()
        super.onCleared()
    }
}
