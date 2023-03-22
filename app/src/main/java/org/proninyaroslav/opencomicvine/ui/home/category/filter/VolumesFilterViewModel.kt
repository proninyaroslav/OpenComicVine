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

package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilterBundle
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class VolumesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            VolumesFilterEvent,
            VolumesFilterState,
            VolumesFilterEffect,
            >(VolumesFilterState.Initial) {

    private val stateCache = FilterStateCache<Nothing, PrefRecentVolumesFilterBundle>(
        FilterStateCache.State(
            filter = VolumesFilterState.Initial.filterBundle,
        )
    )

    init {
        viewModelScope.launch {
            val currentFilters = pref.recentVolumesFilters.first()
            stateCache.save(FilterStateCache.State(filter = currentFilters))
            emitState(
                VolumesFilterState.Loaded(
                    filterBundle = currentFilters,
                )
            )
        }

        on<VolumesFilterEvent.ChangeFilters> { event ->
            emitState(
                VolumesFilterState.FiltersChanged(
                    filterBundle = event.filterBundle,
                    isNeedApply = event.filterBundle != stateCache.current.filter,
                )
            )
        }

        on<VolumesFilterEvent.Apply> {
            val value = state.value
            val newState = VolumesFilterState.Applied(
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setRecentVolumesFilters(filterBundle)
                    stateCache.save(FilterStateCache.State(filter = filterBundle))
                }
                emitEffect(VolumesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface VolumesFilterEvent {
    data class ChangeFilters(
        val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterEvent

    object Apply : VolumesFilterEvent
}

sealed interface VolumesFilterState {
    val filterBundle: PrefRecentVolumesFilterBundle

    object Initial : VolumesFilterState {
        override val filterBundle: PrefRecentVolumesFilterBundle =
            PrefRecentVolumesFilterBundle(
                dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
            )
    }

    data class Loaded(
        override val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterState

    data class FiltersChanged(
        override val filterBundle: PrefRecentVolumesFilterBundle,
        val isNeedApply: Boolean,
    ) : VolumesFilterState

    data class Applied(
        override val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterState
}

sealed interface VolumesFilterEffect {
    object Applied : VolumesFilterEffect
}
