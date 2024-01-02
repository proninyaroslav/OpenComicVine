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
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesSort
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
            val currentSort = pref.wikiIssuesSort.first()
            val currentFilters = pref.wikiIssuesFilters.first()
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

    fun changeSort(sort: PrefWikiIssuesSort) {
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

    fun changeFilters(filterBundle: PrefWikiIssuesFilterBundle) {
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
                pref.setWikiIssuesSort(sort)
                pref.setWikiIssuesFilters(filterBundle)
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

    private fun isNeedApply(sort: PrefWikiIssuesSort, filter: PrefWikiIssuesFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface IssuesFilterState {
    val sort: PrefWikiIssuesSort
    val filterBundle: PrefWikiIssuesFilterBundle

    object Initial : IssuesFilterState {
        override val sort: PrefWikiIssuesSort = PrefWikiIssuesSort.Unknown
        override val filterBundle: PrefWikiIssuesFilterBundle = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Unknown,
            dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
            storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Unknown,
        )
    }

    data class Loaded(
        override val sort: PrefWikiIssuesSort,
        override val filterBundle: PrefWikiIssuesFilterBundle,
    ) : IssuesFilterState

    data class SortChanged(
        override val sort: PrefWikiIssuesSort,
        override val filterBundle: PrefWikiIssuesFilterBundle,
        val isNeedApply: Boolean,
    ) : IssuesFilterState

    data class FiltersChanged(
        override val sort: PrefWikiIssuesSort,
        override val filterBundle: PrefWikiIssuesFilterBundle,
        val isNeedApply: Boolean,
    ) : IssuesFilterState

    data class Applied(
        override val sort: PrefWikiIssuesSort,
        override val filterBundle: PrefWikiIssuesFilterBundle,
    ) : IssuesFilterState
}