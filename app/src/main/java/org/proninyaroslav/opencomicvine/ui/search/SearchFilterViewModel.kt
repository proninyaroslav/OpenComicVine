package org.proninyaroslav.opencomicvine.ui.search

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilterBundle
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            SearchFilterEvent,
            SearchFilterState,
            SearchFilterEffect,
            >(SearchFilterState.Initial) {

    private val stateCache = FilterStateCache<Nothing, PrefSearchFilterBundle>(
        FilterStateCache.State(filter = SearchFilterState.Initial.filterBundle)
    )

    init {
        viewModelScope.launch {
            val currentFilter = pref.searchFilter.first()
            stateCache.save(FilterStateCache.State(filter = currentFilter))
            emitState(
                SearchFilterState.Loaded(
                    filterBundle = currentFilter,
                )
            )
        }

        on<SearchFilterEvent.ChangeFilters> { event ->
            emitState(
                SearchFilterState.FiltersChanged(
                    filterBundle = event.filter,
                    isNeedApply = event.filter != stateCache.current.filter,
                )
            )
        }

        on<SearchFilterEvent.Apply> {
            val value = state.value
            val newState = SearchFilterState.Applied(
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setSearchFilter(filterBundle)
                    stateCache.save(FilterStateCache.State(filter = filterBundle))
                }
                emitEffect(SearchFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface SearchFilterEvent {
    data class ChangeFilters(
        val filter: PrefSearchFilterBundle,
    ) : SearchFilterEvent

    object Apply : SearchFilterEvent
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

sealed interface SearchFilterEffect {
    object Applied : SearchFilterEffect
}