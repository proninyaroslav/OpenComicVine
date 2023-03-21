package org.proninyaroslav.opencomicvine.ui.viewmodel

import androidx.core.util.Pair
import dagger.hilt.android.lifecycle.HiltViewModel

import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor() :
    StoreViewModel<
            DatePickerEvent,
            DatePickerState,
            Unit
            >(DatePickerState.Initial) {

    init {
        on<DatePickerEvent.Show<*>> { event ->
            emitState(
                DatePickerState.Show(
                    dialogType = event.dialogType,
                    range = event.range,
                )
            )
        }

        on<DatePickerEvent.Hide> {
            emitState(DatePickerState.Hide)
        }
    }
}

sealed interface DatePickerEvent {
    data class Show<T>(
        val dialogType: T,
        val range: Pair<Long, Long>?
    ) : DatePickerEvent

    object Hide : DatePickerEvent
}

sealed interface DatePickerState {
    object Initial : DatePickerState

    data class Show<T>(
        val dialogType: T,
        val range: Pair<Long, Long>?
    ) : DatePickerState

    object Hide : DatePickerState
}