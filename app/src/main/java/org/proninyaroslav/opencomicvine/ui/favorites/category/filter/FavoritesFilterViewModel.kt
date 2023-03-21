package org.proninyaroslav.opencomicvine.ui.favorites.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            FavoritesFilterEvent,
            FavoritesFilterState,
            FavoritesFilterEffect,
            >(FavoritesFilterState.Initial) {

    private val stateCache = FilterStateCache<PrefFavoritesSort, Nothing>(
        FilterStateCache.State(sort = FavoritesFilterState.Initial.sort)
    )

    init {
        viewModelScope.launch {
            val currentSort = pref.favoriteCharactersSort.first()
            stateCache.save(FilterStateCache.State(sort = currentSort))
            emitState(
                FavoritesFilterState.Loaded(
                    sort = currentSort,
                )
            )
        }

        on<FavoritesFilterEvent.ChangeSort> { event ->
            emitState(
                FavoritesFilterState.SortChanged(
                    sort = event.sort,
                    isNeedApply = event.sort != stateCache.current.sort,
                )
            )
        }

        on<FavoritesFilterEvent.Apply> {
            val value = state.value
            val newState = FavoritesFilterState.Applied(
                sort = value.sort,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setFavoriteCharactersSort(sort)
                    stateCache.save(FilterStateCache.State(sort = sort))
                }
                emitEffect(FavoritesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface FavoritesFilterEvent {
    data class ChangeSort(
        val sort: PrefFavoritesSort,
    ) : FavoritesFilterEvent

    object Apply : FavoritesFilterEvent
}

sealed interface FavoritesFilterState {
    val sort: PrefFavoritesSort

    object Initial : FavoritesFilterState {
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

sealed interface FavoritesFilterEffect {
    object Applied : FavoritesFilterEffect
}