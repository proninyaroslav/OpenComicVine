package org.proninyaroslav.opencomicvine.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.item.SearchItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.SearchSource
import org.proninyaroslav.opencomicvine.model.paging.SearchSourceFactory
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchSourceFactory: SearchSourceFactory,
    private val errorReportService: ErrorReportService,
) : StoreViewModel<SearchEvent, SearchState, SearchEffect>(SearchState.Initial) {
    private val queryState = MutableStateFlow(state.value.toQueryState())

    val searchList: Flow<PagingData<SearchItem>> = Pager(
        config = PagingConfig(
            pageSize = ComicVineSource.DEFAULT_PAGE_SIZE,
        ),
        pagingSourceFactory = {
            searchSourceFactory.create(queryState = queryState)
        },
    ).flow.cachedIn(viewModelScope)

    init {
        on<SearchEvent.ChangeQuery> { event ->
            viewModelScope.launch {
                val query = event.query.trim()
                val state = SearchState.QueryChanged(query)
                queryState.emit(state.toQueryState())
                emitState(state)
            }
        }

        on<SearchEvent.Search> {
            val stateValue = state.value
            if (stateValue is SearchState.Submitted) {
                return@on
            }
            emitState(SearchState.Submitted(state.value.query))
            emitEffect(SearchEffect.Refresh)
        }

        on<SearchEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineSource.stateToError(state)

    private fun SearchState.toQueryState() = when {
        this is SearchState.Initial || query.isBlank() -> SearchSource.Query.Empty
        else -> SearchSource.Query.Value(query)
    }
}

sealed interface SearchEvent {
    data class ChangeQuery(val query: String) : SearchEvent

    object Search : SearchEvent

    data class ErrorReport(val info: ErrorReportInfo) : SearchEvent
}

sealed interface SearchState {
    val query: String

    object Initial : SearchState {
        override val query = ""
    }

    data class QueryChanged(override val query: String) : SearchState

    data class Submitted(override val query: String) : SearchState
}

sealed interface SearchEffect {
    object Refresh : SearchEffect
}