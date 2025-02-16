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

package org.proninyaroslav.opencomicvine.ui.favorites.category.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import javax.inject.Inject

@HiltViewModel
class FavoritesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<FavoritesFilterState>(FavoritesFilterState.Initial)
    private val stateCache = FilterStateCache<PrefFavoritesSort, Nothing>(
        FilterStateCache.State(sort = FavoritesFilterState.Initial.sort)
    )

    val state: StateFlow<FavoritesFilterState> = _state

    init {
        viewModelScope.launch {
            val currentSort = pref.favoriteCharactersSort.first()
            stateCache.save(FilterStateCache.State(sort = currentSort))
            _state.value = FavoritesFilterState.Loaded(
                sort = currentSort,
            )
        }
    }

    fun changeSort(sort: PrefFavoritesSort) {
        _state.value = FavoritesFilterState.SortChanged(
            sort = sort,
            isNeedApply = sort != stateCache.current.sort,
        )
    }

    fun apply() {
        val value = state.value
        val newState = FavoritesFilterState.Applied(
            sort = value.sort,
        )
        viewModelScope.launch {
            newState.run {
                pref.setFavoriteCharactersSort(sort)
                stateCache.save(FilterStateCache.State(sort = sort))
            }
            _state.value = newState
        }
    }
}

sealed interface FavoritesFilterState {
    val sort: PrefFavoritesSort

    data object Initial : FavoritesFilterState {
        override val sort: PrefFavoritesSort = PrefFavoritesSort.Unknown
    }

    data class Loaded(
        override val sort: PrefFavoritesSort,
    ) : FavoritesFilterState

    data class SortChanged(
        override val sort: PrefFavoritesSort,
        val isNeedApply: Boolean,
    ) : FavoritesFilterState

    data class Applied(
        override val sort: PrefFavoritesSort,
    ) : FavoritesFilterState
}