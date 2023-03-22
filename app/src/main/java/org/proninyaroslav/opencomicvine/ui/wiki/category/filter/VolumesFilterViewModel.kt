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
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesSort
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

    private val stateCache = FilterStateCache(
        FilterStateCache.State(
            sort = VolumesFilterState.Initial.sort,
            filter = VolumesFilterState.Initial.filterBundle,
        )
    )

    init {
        viewModelScope.launch {
            val currentSort = pref.wikiVolumesSort.first()
            val currentFilters = pref.wikiVolumesFilters.first()
            stateCache.save(
                FilterStateCache.State(
                    sort = currentSort,
                    filter = currentFilters,
                )
            )
            emitState(
                VolumesFilterState.Loaded(
                    sort = currentSort,
                    filterBundle = currentFilters,
                )
            )
        }

        on<VolumesFilterEvent.ChangeSort> { event ->
            state.value.run {
                emitState(
                    VolumesFilterState.SortChanged(
                        sort = event.sort,
                        filterBundle = filterBundle,
                        isNeedApply = isNeedApply(
                            sort = event.sort,
                            filter = filterBundle,
                        ),
                    )
                )
            }
        }

        on<VolumesFilterEvent.ChangeFilters> { event ->
            state.value.run {
                emitState(
                    VolumesFilterState.FiltersChanged(
                        sort = sort,
                        filterBundle = event.filterBundle,
                        isNeedApply = isNeedApply(
                            sort = sort,
                            filter = event.filterBundle,
                        ),
                    )
                )
            }
        }

        on<VolumesFilterEvent.Apply> {
            val value = state.value
            val newState = VolumesFilterState.Applied(
                sort = value.sort,
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setWikiVolumesSort(sort)
                    pref.setWikiVolumesFilters(filterBundle)
                    stateCache.save(
                        FilterStateCache.State(
                            sort = sort,
                            filter = filterBundle,
                        )
                    )
                }
                emitEffect(VolumesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }

    private fun isNeedApply(sort: PrefWikiVolumesSort, filter: PrefWikiVolumesFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface VolumesFilterEvent {
    data class ChangeSort(
        val sort: PrefWikiVolumesSort
    ) : VolumesFilterEvent

    data class ChangeFilters(
        val filterBundle: PrefWikiVolumesFilterBundle,
    ) : VolumesFilterEvent

    object Apply : VolumesFilterEvent
}

sealed interface VolumesFilterState {
    val sort: PrefWikiVolumesSort
    val filterBundle: PrefWikiVolumesFilterBundle

    object Initial : VolumesFilterState {
        override val sort: PrefWikiVolumesSort = PrefWikiVolumesSort.Unknown
        override val filterBundle: PrefWikiVolumesFilterBundle = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Unknown,
            dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
        )
    }

    data class Loaded(
        override val sort: PrefWikiVolumesSort,
        override val filterBundle: PrefWikiVolumesFilterBundle,
    ) : VolumesFilterState

    data class SortChanged(
        override val sort: PrefWikiVolumesSort,
        override val filterBundle: PrefWikiVolumesFilterBundle,
        val isNeedApply: Boolean,
    ) : VolumesFilterState

    data class FiltersChanged(
        override val sort: PrefWikiVolumesSort,
        override val filterBundle: PrefWikiVolumesFilterBundle,
        val isNeedApply: Boolean,
    ) : VolumesFilterState

    data class Applied(
        override val sort: PrefWikiVolumesSort,
        override val filterBundle: PrefWikiVolumesFilterBundle,
    ) : VolumesFilterState
}

sealed interface VolumesFilterEffect {
    object Applied : VolumesFilterEffect
}
