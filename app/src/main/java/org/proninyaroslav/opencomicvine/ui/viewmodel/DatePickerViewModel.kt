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

package org.proninyaroslav.opencomicvine.ui.viewmodel

import androidx.core.util.Pair
import dagger.hilt.android.lifecycle.HiltViewModel

import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor() :
    StoreViewModel<
            DatePickerEvent,
            DatePickerState,
            Unit
            >(DatePickerState.Initial) {

    init {
        on<DatePickerEvent.Show<*>> { event ->
            emitState(
                DatePickerState.Show(
                    dialogType = event.dialogType,
                    range = event.range,
                )
            )
        }

        on<DatePickerEvent.Hide> {
            emitState(DatePickerState.Hide)
        }
    }
}

sealed interface DatePickerEvent {
    data class Show<T>(
        val dialogType: T,
        val range: Pair<Long, Long>?
    ) : DatePickerEvent

    object Hide : DatePickerEvent
}

sealed interface DatePickerState {
    object Initial : DatePickerState

    data class Show<T>(
        val dialogType: T,
        val range: Pair<Long, Long>?
    ) : DatePickerState

    object Hide : DatePickerState
}
