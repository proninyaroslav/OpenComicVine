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

package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.preferences.PrefVolumeIssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import javax.inject.Inject

@HiltViewModel
class VolumeIssuesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<VolumeIssuesFilterState>(VolumeIssuesFilterState.Initial)
    val state: StateFlow<VolumeIssuesFilterState> = _state

    init {
        viewModelScope.launch {
            val currentSort = pref.volumeIssuesSort.first()
            _state.value = VolumeIssuesFilterState.Loaded(
                sort = currentSort,
            )
        }
    }

    fun changeSort(sort: PrefVolumeIssuesSort) {
        state.value.run {
            _state.value = VolumeIssuesFilterState.SortChanged(sort = sort)
        }
        val value = state.value
        val newState = VolumeIssuesFilterState.Applied(
            sort = value.sort,
        )
        viewModelScope.launch {
            saveToPref(newState)
            _state.value = newState
        }
    }

    private suspend fun saveToPref(state: VolumeIssuesFilterState) {
        state.run {
            pref.setVolumeIssuesSort(sort)
        }
    }
}

sealed interface VolumeIssuesFilterState {
    val sort: PrefVolumeIssuesSort

    data object Initial : VolumeIssuesFilterState {
        override val sort: PrefVolumeIssuesSort = PrefVolumeIssuesSort.Unknown
    }

    data class Loaded(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState

    data class SortChanged(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState

    data class Applied(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState
}
