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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefVolumeIssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class VolumeIssuesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            VolumeIssuesFilterEvent,
            VolumeIssuesFilterState,
            VolumeIssuesFilterEffect,
            >(VolumeIssuesFilterState.Initial) {

    init {
        viewModelScope.launch {
            val currentSort = pref.volumeIssuesSort.first()
            emitState(
                VolumeIssuesFilterState.Loaded(
                    sort = currentSort,
                )
            )
        }

        on<VolumeIssuesFilterEvent.ChangeSort> { event ->
            state.value.run {
                emitState(
                    VolumeIssuesFilterState.SortChanged(
                        sort = event.sort,
                    )
                )
            }
            val value = state.value
            val newState = VolumeIssuesFilterState.Applied(
                sort = value.sort,
            )
            viewModelScope.launch {
                saveToPref(newState)
                emitEffect(VolumeIssuesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }

    private suspend fun saveToPref(state: VolumeIssuesFilterState) {
        state.run {
            pref.setVolumeIssuesSort(sort)
        }
    }
}

sealed interface VolumeIssuesFilterEvent {
    data class ChangeSort(
        val sort: PrefVolumeIssuesSort
    ) : VolumeIssuesFilterEvent
}

sealed interface VolumeIssuesFilterState {
    val sort: PrefVolumeIssuesSort

    object Initial : VolumeIssuesFilterState {
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

sealed interface VolumeIssuesFilterEffect {
    object Applied : VolumeIssuesFilterEffect
}
