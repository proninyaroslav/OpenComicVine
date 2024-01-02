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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentIssuesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentIssuesFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentIssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import javax.inject.Inject

@HiltViewModel
class IssuesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<IssuesFilterState>(IssuesFilterState.Initial)
    private val stateCache = FilterStateCache(
        FilterStateCache.State(
            sort = IssuesFilterState.Initial.sort,
            filter = IssuesFilterState.Initial.filterBundle,
        )
    )

    val state: StateFlow<IssuesFilterState> = _state

    init {
        viewModelScope.launch {
            val currentSort = pref.recentIssuesSort.first()
            val currentFilters = pref.recentIssuesFilters.first()
            stateCache.save(
                FilterStateCache.State(
                    sort = currentSort,
                    filter = currentFilters,
                )
            )
            _state.value = IssuesFilterState.Loaded(
                sort = currentSort,
                filterBundle = currentFilters,
            )
        }
    }

    fun changeSort(sort: PrefRecentIssuesSort) {
        state.value.run {
            _state.value = IssuesFilterState.SortChanged(
                sort = sort,
                filterBundle = filterBundle,
                isNeedApply = isNeedApply(
                    sort = sort,
                    filter = filterBundle,
                )
            )
        }
    }

    fun changeFilters(filterBundle: PrefRecentIssuesFilterBundle) {
        state.value.run {
            _state.value = IssuesFilterState.FiltersChanged(
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
        val newState = IssuesFilterState.Applied(
            sort = value.sort,
            filterBundle = value.filterBundle,
        )
        viewModelScope.launch {
            newState.run {
                pref.setRecentIssuesSort(sort)
                pref.setRecentIssuesFilters(filterBundle)
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

    private fun isNeedApply(sort: PrefRecentIssuesSort, filter: PrefRecentIssuesFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface IssuesFilterState {
    val sort: PrefRecentIssuesSort
    val filterBundle: PrefRecentIssuesFilterBundle

    object Initial : IssuesFilterState {
        override val sort: PrefRecentIssuesSort = PrefRecentIssuesSort.Unknown
        override val filterBundle: PrefRecentIssuesFilterBundle = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
            storeDate = PrefRecentIssuesFilter.StoreDate.Unknown,
        )
    }

    data class Loaded(
        override val sort: PrefRecentIssuesSort,
        override val filterBundle: PrefRecentIssuesFilterBundle,
    ) : IssuesFilterState

    data class FiltersChanged(
        override val sort: PrefRecentIssuesSort,
        override val filterBundle: PrefRecentIssuesFilterBundle,
        val isNeedApply: Boolean,
    ) : IssuesFilterState

    data class SortChanged(
        override val sort: PrefRecentIssuesSort,
        override val filterBundle: PrefRecentIssuesFilterBundle,
        val isNeedApply: Boolean,
    ) : IssuesFilterState

    data class Applied(
        override val sort: PrefRecentIssuesSort,
        override val filterBundle: PrefRecentIssuesFilterBundle,
    ) : IssuesFilterState
}