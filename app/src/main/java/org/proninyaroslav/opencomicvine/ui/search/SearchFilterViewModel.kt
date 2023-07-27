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

package org.proninyaroslav.opencomicvine.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilterBundle
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<SearchFilterState>(SearchFilterState.Initial)
    private val stateCache = FilterStateCache<Nothing, PrefSearchFilterBundle>(
        FilterStateCache.State(filter = SearchFilterState.Initial.filterBundle)
    )
    
    val state: StateFlow<SearchFilterState> = _state

    init {
        viewModelScope.launch {
            val currentFilter = pref.searchFilter.first()
            stateCache.save(FilterStateCache.State(filter = currentFilter))
            _state.value = SearchFilterState.Loaded(
                filterBundle = currentFilter,
            )
        }
    }

    fun changeFilters(filter: PrefSearchFilterBundle) {
        _state.value = SearchFilterState.FiltersChanged(
            filterBundle = filter,
            isNeedApply = filter != stateCache.current.filter,
        )
    }

    fun apply() {
        val value = state.value
        val newState = SearchFilterState.Applied(
            filterBundle = value.filterBundle,
        )
        viewModelScope.launch {
            newState.run {
                pref.setSearchFilter(filterBundle)
                stateCache.save(FilterStateCache.State(filter = filterBundle))
            }
            _state.value = SearchFilterState.Applied(filterBundle = value.filterBundle)
        }
    }
}

sealed interface SearchFilterState {
    val filterBundle: PrefSearchFilterBundle

    object Initial : SearchFilterState {
        override val filterBundle: PrefSearchFilterBundle = PrefSearchFilterBundle(
            resources = PrefSearchFilter.Resources.Unknown
        )
    }

    data class Loaded(
        override val filterBundle: PrefSearchFilterBundle,
    ) : SearchFilterState

    data class FiltersChanged(
        override val filterBundle: PrefSearchFilterBundle,
        val isNeedApply: Boolean,
    ) : SearchFilterState

    data class Applied(
        override val filterBundle: PrefSearchFilterBundle,
    ) : SearchFilterState
}