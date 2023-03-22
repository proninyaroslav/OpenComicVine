/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.model.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

open class StoreViewModel<Event : Any, State : Any, Effect : Any>(
    initialState: State
) : Store<Event, State, Effect>, ViewModel() {

    private val eventMap: MutableMap<KClass<out Event>, (Event) -> Unit> =
        mutableMapOf()
    private val _state = MutableStateFlow(initialState)
    private val _effect = Channel<Effect>()
    private val _event = MutableSharedFlow<Event>()

    override val state: StateFlow<State> = _state

    override val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        handleEvents()
    }

    override fun event(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    override fun emitState(state: State) {
        _state.value = state
    }

    override fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified E : Event> on(
        noinline handler: (E) -> Unit
    ) {
        `access$eventMap`[E::class] = handler as (Event) -> Unit
    }

    @Suppress("PropertyName", "unused")
    @PublishedApi
    internal val `access$eventMap`: MutableMap<KClass<out Event>, (Event) -> Unit>
        get() = eventMap

    private fun handleEvents() {
        viewModelScope.launch {
            _event.collect(::handleEvent)
        }
    }

    private fun handleEvent(event: Event) {
        eventMap[event::class]?.let { eventHandler ->
            eventHandler(event)
        }
    }
}
