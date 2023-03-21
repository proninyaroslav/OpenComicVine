package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefVolumeIssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class VolumeIssuesFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            VolumeIssuesFilterEvent,
            VolumeIssuesFilterState,
            VolumeIssuesFilterEffect,
            >(VolumeIssuesFilterState.Initial) {

    init {
        viewModelScope.launch {
            val currentSort = pref.volumeIssuesSort.first()
            emitState(
                VolumeIssuesFilterState.Loaded(
                    sort = currentSort,
                )
            )
        }

        on<VolumeIssuesFilterEvent.ChangeSort> { event ->
            state.value.run {
                emitState(
                    VolumeIssuesFilterState.SortChanged(
                        sort = event.sort,
                    )
                )
            }
            val value = state.value
            val newState = VolumeIssuesFilterState.Applied(
                sort = value.sort,
            )
            viewModelScope.launch {
                saveToPref(newState)
                emitEffect(VolumeIssuesFilterEffect.Applied)
                emitState(newState)
            }
        }
    }

    private suspend fun saveToPref(state: VolumeIssuesFilterState) {
        state.run {
            pref.setVolumeIssuesSort(sort)
        }
    }
}

sealed interface VolumeIssuesFilterEvent {
    data class ChangeSort(
        val sort: PrefVolumeIssuesSort
    ) : VolumeIssuesFilterEvent
}

sealed interface VolumeIssuesFilterState {
    val sort: PrefVolumeIssuesSort

    object Initial : VolumeIssuesFilterState {
        override val sort: PrefVolumeIssuesSort = PrefVolumeIssuesSort.Unknown
    }

    data class Loaded(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState

    data class SortChanged(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState

    data class Applied(
        override val sort: PrefVolumeIssuesSort,
    ) : VolumeIssuesFilterState
}

sealed interface VolumeIssuesFilterEffect {
    object Applied : VolumeIssuesFilterEffect
}