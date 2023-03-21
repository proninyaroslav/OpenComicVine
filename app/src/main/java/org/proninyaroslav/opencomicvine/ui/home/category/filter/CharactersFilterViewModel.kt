package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilterBundle
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

    private val stateCache = FilterStateCache<Nothing, PrefRecentCharactersFilterBundle>(
        FilterStateCache.State(filter = CharactersFilterState.Initial.filterBundle)
    )

    init {
        viewModelScope.launch {
            val currentFilters = pref.recentCharactersFilters.first()
            stateCache.save(FilterStateCache.State(filter = currentFilters))
            emitState(
                CharactersFilterState.Loaded(
                    filterBundle = currentFilters,
                )
            )
        }

        on<CharactersFilterEvent.ChangeFilters> { event ->
            emitState(
                CharactersFilterState.FiltersChanged(
                    filterBundle = event.filterBundle,
                    isNeedApply = event.filterBundle != stateCache.current.filter,
                )
            )
        }

        on<CharactersFilterEvent.Apply> {
            val value = state.value
            val newState = CharactersFilterState.Applied(
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setRecentCharactersFilters(filterBundle)
                    stateCache.save(FilterStateCache.State(filter = filterBundle))
                }
                emitEffect(CharactersFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface CharactersFilterEvent {
    data class ChangeFilters(
        val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterEvent

    object Apply : CharactersFilterEvent
}

sealed interface CharactersFilterState {
    val filterBundle: PrefRecentCharactersFilterBundle

    object Initial : CharactersFilterState {
        override val filterBundle: PrefRecentCharactersFilterBundle =
            PrefRecentCharactersFilterBundle(
                dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown,
            )
    }

    data class Loaded(
        override val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterState

    data class FiltersChanged(
        override val filterBundle: PrefRecentCharactersFilterBundle,
        val isNeedApply: Boolean,
    ) : CharactersFilterState

    data class Applied(
        override val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterState
}

sealed interface CharactersFilterEffect {
    object Applied : CharactersFilterEffect
}