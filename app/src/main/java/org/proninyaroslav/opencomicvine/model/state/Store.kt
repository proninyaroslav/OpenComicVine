package org.proninyaroslav.opencomicvine.model.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Store<Event, State, Effect> {
    val state: StateFlow<State>

    val effect: Flow<Effect>

    fun event(event: Event)

    fun emitState(state: State)

    fun emitEffect(effect: Effect)
}