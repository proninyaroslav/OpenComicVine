package org.proninyaroslav.opencomicvine.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManager
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkConnectionViewModel @Inject constructor(
    private val connectivityManager: AppConnectivityManager,
) : StoreViewModel<
        Unit,
        NetworkState,
        NetworkEffect>(NetworkState.Initial) {

    init {
        emitState(
            if (connectivityManager.isNetworkAvailable()) {
                NetworkState.ConnectionAvailable
            } else {
                NetworkState.NoConnection
            }
        )
        viewModelScope.launch {
            connectivityManager.observeNetworkAvailability
                .collect { isAvailable ->
                    if (isAvailable && state.value is NetworkState.NoConnection) {
                        emitEffect(NetworkEffect.Reestablished)
                    }
                    emitState(
                        if (isAvailable) {
                            NetworkState.ConnectionAvailable
                        } else {
                            NetworkState.NoConnection
                        }
                    )
                }
        }
    }
}

sealed interface NetworkState {
    object Initial : NetworkState

    object NoConnection : NetworkState

    object ConnectionAvailable : NetworkState
}

sealed interface NetworkEffect {
    object Reestablished : NetworkEffect
}