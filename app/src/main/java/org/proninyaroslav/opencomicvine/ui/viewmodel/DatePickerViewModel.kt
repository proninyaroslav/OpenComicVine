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
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<DatePickerState>(DatePickerState.Initial)
    val state: StateFlow<DatePickerState> = _state

    fun <T> show(dialogType: T, range: Pair<Long, Long>?) {
        _state.value = DatePickerState.Show(
            dialogType = dialogType,
            range = range,
        )
    }

    fun hide() {
        _state.value = DatePickerState.Hide
    }
}

sealed interface DatePickerState {
    data object Initial : DatePickerState

    data class Show<T>(
        val dialogType: T,
        val range: Pair<Long, Long>?
    ) : DatePickerState

    data object Hide : DatePickerState
}
