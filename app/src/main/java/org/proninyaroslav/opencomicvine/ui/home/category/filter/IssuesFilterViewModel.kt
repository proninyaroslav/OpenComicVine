package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class IssuesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            IssuesFilterEvent,
            IssuesFilterState,
            IssuesFilterEffect,
            >(IssuesFilterState.Initial) {

    private val stateCache = FilterStateCache(
        FilterStateCache.State(
            sort = IssuesFilterState.Initial.sort,
            filter = IssuesFilterState.Initial.filterBundle,
        )
    )

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
            emitState(
                IssuesFilterState.Loaded(
                    sort = currentSort,
                    filterBundle = currentFilters,
                )
            )
        }

        on<IssuesFilterEvent.ChangeSort> { event ->
            state.value.run {
                emitState(
                    IssuesFilterState.SortChanged(
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

        on<IssuesFilterEvent.ChangeFilters> { event ->
            state.value.run {
                emitState(
                    IssuesFilterState.FiltersChanged(
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

        on<IssuesFilterEvent.Apply> {
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
                emitEffect(IssuesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }

    private fun isNeedApply(sort: PrefRecentIssuesSort, filter: PrefRecentIssuesFilterBundle) =
        stateCache.current.let { sort != it.sort || filter != it.filter }
}

sealed interface IssuesFilterEvent {
    data class ChangeSort(
        val sort: PrefRecentIssuesSort,
    ) : IssuesFilterEvent

    data class ChangeFilters(
        val filterBundle: PrefRecentIssuesFilterBundle,
    ) : IssuesFilterEvent

    object Apply : IssuesFilterEvent
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

sealed interface IssuesFilterEffect {
    object Applied : IssuesFilterEffect
}