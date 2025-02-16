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
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.item.SearchItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.SearchSource
import org.proninyaroslav.opencomicvine.model.paging.SearchSourceFactory
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchSourceFactory: SearchSourceFactory,
    private val errorReportService: ErrorReportService,
) : ViewModel() {
    private val _state = MutableStateFlow<SearchState>(SearchState.Initial)
    private val queryState: Flow<SearchSource.Query> = _state.map { it.toQueryState() }

    val state: StateFlow<SearchState> = _state

    val searchList: Flow<PagingData<SearchItem>> = Pager(
        config = PagingConfig(
            pageSize = ComicVineSource.DEFAULT_PAGE_SIZE,
        ),
        pagingSourceFactory = {
            searchSourceFactory.create(queryState = queryState)
        },
    ).flow.cachedIn(viewModelScope)

    fun changeQuery(query: String) {
        _state.value = SearchState.QueryChanged(query.trim())
    }

    fun search() {
        if (_state.value is SearchState.Submitted) {
            return
        }
        _state.value = SearchState.Submitted(_state.value.query)
    }

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineSource.stateToError(state)

    private fun SearchState.toQueryState() =
        if (query.isBlank()) {
            SearchSource.Query.Empty
        } else {
            SearchSource.Query.Value(query)
        }
}

sealed interface SearchState {
    val query: String

    data object Initial : SearchState {
        override val query = ""
    }

    data class QueryChanged(override val query: String) : SearchState

    data class Submitted(override val query: String) : SearchState
}