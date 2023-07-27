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

package org.proninyaroslav.opencomicvine.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManager
import javax.inject.Inject

@HiltViewModel
class NetworkConnectionViewModel @Inject constructor(
    private val connectivityManager: AppConnectivityManager,
) : ViewModel() {
    private val _state = MutableStateFlow<NetworkState>(NetworkState.Initial)
    val state: StateFlow<NetworkState> = _state

    init {
        _state.value = if (connectivityManager.isNetworkAvailable()) {
            NetworkState.ConnectionAvailable
        } else {
            NetworkState.NoConnection
        }
        viewModelScope.launch {
            connectivityManager.observeNetworkAvailability
                .collect { isAvailable ->
                    _state.value = if (isAvailable) {
                        if (state.value is NetworkState.NoConnection) {
                            NetworkState.Reestablished
                        } else {
                            NetworkState.ConnectionAvailable
                        }
                    } else {
                        NetworkState.NoConnection
                    }
                }
        }
    }
}

sealed interface NetworkState {
    object Initial : NetworkState

    object NoConnection : NetworkState

    object ConnectionAvailable : NetworkState

    object Reestablished : NetworkState
}