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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class CharactersFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            CharactersFilterEvent,
            CharactersFilterState,
            CharactersFilterEffect,
            >(CharactersFilterState.Initial) {

    private val stateCache = FilterStateCache(
        FilterStateCache.State(
            sort = CharactersFilterState.Initial.sort,
            filter = CharactersFilterState.Initial.filterBundle,
        )
    )

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
            emitState(
                CharactersFilterState.Loaded(
                    sort = currentSort,
                    filterBundle = currentFilters,
                )
            )
        }

        on<CharactersFilterEvent.ChangeSort> { event ->
            state.value.run {
                emitState(
                    CharactersFilterState.SortChanged(
                        sort = event.sort,
                        filterBundle = filterBundle,
                        isNeedApply = isNeedApply(
                            sort = event.sort,
                            filter = filterBundle,
                        )
                    )
                )
            }
        }

        on<CharactersFilterEvent.ChangeFilters> { event ->
            state.value.run {
                emitState(
                    CharactersFilterState.FiltersChanged(
                        sort = sort,
                        filterBundle = event.filterBundle,
                        isNeedApply = isNeedApply(
                            sort = sort,
                            filter = event.filterBundle,
                        )
                    )
                )
            }
        }

        on<CharactersFilterEvent.Apply> {
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
                emitEffect(CharactersFilterEffect.Applied)
                emitState(newState)
            }
        }
    }

    private fun isNeedApply(sort: PrefWikiCharactersSort, filter: PrefWikiCharactersFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface CharactersFilterEvent {
    data class ChangeSort(
        val sort: PrefWikiCharactersSort
    ) : CharactersFilterEvent

    data class ChangeFilters(
        val filterBundle: PrefWikiCharactersFilterBundle,
    ) : CharactersFilterEvent

    object Apply : CharactersFilterEvent
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

sealed interface CharactersFilterEffect {
    object Applied : CharactersFilterEffect
}
