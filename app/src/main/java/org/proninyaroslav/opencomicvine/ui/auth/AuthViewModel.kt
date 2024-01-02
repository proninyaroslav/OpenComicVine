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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiKeyRepo: ApiKeyRepository,
    private val errorReportService: ErrorReportService,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun changeApiKey(apiKey: String) {
        _state.value = AuthState(apiKey = apiKey.trim())
    }

    val submit = Submit()

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

    inner class Submit {
        private val _submitState = MutableStateFlow<AuthSubmitState>(AuthSubmitState.Initial)
        val state: StateFlow<AuthSubmitState> = _submitState

        operator fun invoke() {
            if (_submitState.value == AuthSubmitState.SubmitInProgress) {
                return
            }
            viewModelScope.launch {
                val currentState = _state.value
                val apiKey = currentState.apiKey

                _submitState.value = AuthSubmitState.SubmitInProgress
                if (apiKey.isBlank()) {
                    _submitState.value = AuthSubmitState.SubmitFailed.EmptyApiKey
                    return@launch
                }
                when (val res = apiKeyRepo.set(apiKey)) {
                    is ApiKeyRepository.SaveResult.Success -> {
                        _submitState.value = AuthSubmitState.Submitted
                    }

                    is ApiKeyRepository.SaveResult.Failed -> {
                        _submitState.value = AuthSubmitState.SubmitFailed.SaveError(
                            error = res,
                        )
                    }
                }
            }
        }
    }
}

data class AuthState(
    val apiKey: String = ""
)

sealed interface AuthSubmitState {
    object Initial : AuthSubmitState

    object SubmitInProgress : AuthSubmitState

    object Submitted : AuthSubmitState

    sealed interface SubmitFailed : AuthSubmitState {
        object EmptyApiKey : SubmitFailed

        data class SaveError(
            val error: ApiKeyRepository.SaveResult.Failed,
        ) : SubmitFailed
    }
}