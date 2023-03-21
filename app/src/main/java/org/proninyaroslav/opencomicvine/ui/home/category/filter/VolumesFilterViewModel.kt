package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilterBundle
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

    private val stateCache = FilterStateCache<Nothing, PrefRecentVolumesFilterBundle>(
        FilterStateCache.State(
            filter = VolumesFilterState.Initial.filterBundle,
        )
    )

    init {
        viewModelScope.launch {
            val currentFilters = pref.recentVolumesFilters.first()
            stateCache.save(FilterStateCache.State(filter = currentFilters))
            emitState(
                VolumesFilterState.Loaded(
                    filterBundle = currentFilters,
                )
            )
        }

        on<VolumesFilterEvent.ChangeFilters> { event ->
            emitState(
                VolumesFilterState.FiltersChanged(
                    filterBundle = event.filterBundle,
                    isNeedApply = event.filterBundle != stateCache.current.filter,
                )
            )
        }

        on<VolumesFilterEvent.Apply> {
            val value = state.value
            val newState = VolumesFilterState.Applied(
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setRecentVolumesFilters(filterBundle)
                    stateCache.save(FilterStateCache.State(filter = filterBundle))
                }
                emitEffect(VolumesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface VolumesFilterEvent {
    data class ChangeFilters(
        val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterEvent

    object Apply : VolumesFilterEvent
}

sealed interface VolumesFilterState {
    val filterBundle: PrefRecentVolumesFilterBundle

    object Initial : VolumesFilterState {
        override val filterBundle: PrefRecentVolumesFilterBundle =
            PrefRecentVolumesFilterBundle(
                dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
            )
    }

    data class Loaded(
        override val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterState

    data class FiltersChanged(
        override val filterBundle: PrefRecentVolumesFilterBundle,
        val isNeedApply: Boolean,
    ) : VolumesFilterState

    data class Applied(
        override val filterBundle: PrefRecentVolumesFilterBundle,
    ) : VolumesFilterState
}

sealed interface VolumesFilterEffect {
    object Applied : VolumesFilterEffect
}