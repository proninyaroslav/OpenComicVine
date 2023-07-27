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

package org.proninyaroslav.opencomicvine.ui.wiki.category.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import javax.inject.Inject

@HiltViewModel
class CharactersFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<CharactersFilterState>(CharactersFilterState.Initial)
    private val stateCache = FilterStateCache(
        FilterStateCache.State(
            sort = CharactersFilterState.Initial.sort,
            filter = CharactersFilterState.Initial.filterBundle,
        )
    )

    val state: StateFlow<CharactersFilterState> = _state

    init {
        viewModelScope.launch {
            val currentSort = pref.wikiCharactersSort.first()
            val currentFilters = pref.wikiCharactersFilters.first()
            stateCache.save(
                FilterStateCache.State(
                    sort = currentSort,
                    filter = currentFilters,
                )
            )
            _state.value = CharactersFilterState.Loaded(
                sort = currentSort,
                filterBundle = currentFilters,
            )
        }
    }

    fun changeSort(sort: PrefWikiCharactersSort) {
        state.value.run {
            _state.value = CharactersFilterState.SortChanged(
                sort = sort,
                filterBundle = filterBundle,
                isNeedApply = isNeedApply(
                    sort = sort,
                    filter = filterBundle,
                )
            )
        }
    }

    fun changeFilters(filterBundle: PrefWikiCharactersFilterBundle) {
        state.value.run {
            _state.value = CharactersFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filterBundle,
                isNeedApply = isNeedApply(
                    sort = sort,
                    filter = filterBundle,
                )
            )
        }
    }

    fun apply() {
        val value = state.value
        val newState = CharactersFilterState.Applied(
            sort = value.sort,
            filterBundle = value.filterBundle,
        )
        viewModelScope.launch {
            newState.run {
                pref.setWikiCharactersSort(sort)
                pref.setWikiCharactersFilters(filterBundle)
                stateCache.save(
                    FilterStateCache.State(
                        sort = sort,
                        filter = filterBundle,
                    )
                )
            }
            _state.value = newState
        }
    }

    private fun isNeedApply(sort: PrefWikiCharactersSort, filter: PrefWikiCharactersFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface CharactersFilterState {
    val sort: PrefWikiCharactersSort
    val filterBundle: PrefWikiCharactersFilterBundle

    object Initial : CharactersFilterState {
        override val sort: PrefWikiCharactersSort = PrefWikiCharactersSort.Unknown
        override val filterBundle: PrefWikiCharactersFilterBundle = PrefWikiCharactersFilterBundle(
            gender = PrefWikiCharactersFilter.Gender.Unknown,
            name = PrefWikiCharactersFilter.Name.Unknown,
            dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
        )
    }

    data class Loaded(
        override val sort: PrefWikiCharactersSort,
        override val filterBundle: PrefWikiCharactersFilterBundle,
    ) : CharactersFilterState

    data class SortChanged(
        override val sort: PrefWikiCharactersSort,
        override val filterBundle: PrefWikiCharactersFilterBundle,
        val isNeedApply: Boolean,
    ) : CharactersFilterState

    data class FiltersChanged(
        override val sort: PrefWikiCharactersSort,
        override val filterBundle: PrefWikiCharactersFilterBundle,
        val isNeedApply: Boolean,
    ) : CharactersFilterState

    data class Applied(
        override val sort: PrefWikiCharactersSort,
        override val filterBundle: PrefWikiCharactersFilterBundle,
    ) : CharactersFilterState
}