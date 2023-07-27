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

package org.proninyaroslav.opencomicvine.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import javax.inject.Inject

@HiltViewModel
class AuthGuardViewModel @Inject constructor(
    apiKeyRepo: ApiKeyRepository,
) : ViewModel() {
    val state: StateFlow<AuthGuardState> = apiKeyRepo.get()
        .map {
            when (it) {
                is ApiKeyRepository.GetResult.Success -> AuthGuardState.Authorized
                ApiKeyRepository.GetResult.Failed.NoApiKey -> AuthGuardState.NotAuthorized
                is ApiKeyRepository.GetResult.Failed.IO -> AuthGuardState.GetStatusError(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthGuardState.Initial,
        )
}

sealed interface AuthGuardState {
    object Initial : AuthGuardState

    object Authorized : AuthGuardState

    object NotAuthorized : AuthGuardState

    data class GetStatusError(val error: ApiKeyRepository.GetResult.Failed.IO) : AuthGuardState
}
