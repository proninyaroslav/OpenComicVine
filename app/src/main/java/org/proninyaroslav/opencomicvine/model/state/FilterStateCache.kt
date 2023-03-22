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

class FilterStateCache<S, F>(initialState: State<S, F>) {
    private val appliedStatesStack = ArrayDeque(listOf(initialState))

    val current
        get() = appliedStatesStack.first()

    fun save(state: State<S, F>) {
        appliedStatesStack.removeFirstOrNull()
        appliedStatesStack.addLast(state)
    }

    data class State<S, F>(
        val sort: S? = null,
        val filter: F? = null,
    )
}
