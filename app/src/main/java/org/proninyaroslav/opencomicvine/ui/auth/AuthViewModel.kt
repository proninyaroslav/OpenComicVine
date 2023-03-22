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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiKeyRepo: ApiKeyRepository,
    private val errorReportService: ErrorReportService,
) : StoreViewModel<AuthEvent, AuthState, AuthEffect>(AuthState.Initial) {

    init {
        on<AuthEvent.ChangeApiKey> { event ->
            if (isChangeNotAllowed()) {
                return@on
            }
            emitState(AuthState.ApiKeyChanged(event.apiKey.trim()))
        }

        on<AuthEvent.Submit> {
            if (isSubmitNotAllowed()) {
                return@on
            }
            viewModelScope.launch { submit() }
        }

        on<AuthEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private suspend fun submit() {
        val currentState = state.value
        val apiKey = currentState.apiKey

        emitState(AuthState.SubmitInProgress(apiKey))
        if (apiKey.isBlank()) {
            emitState(AuthState.SubmitFailed.EmptyApiKey)
            return
        }
        when (val res = apiKeyRepo.set(apiKey)) {
            is ApiKeyRepository.SaveResult.Success -> {
                emitState(AuthState.Submitted(apiKey))
                emitEffect(AuthEffect.Submitted)
            }
            is ApiKeyRepository.SaveResult.Failed -> {
                emitState(
                    AuthState.SubmitFailed.SaveError(
                        apiKey = apiKey,
                        error = res,
                    )
                )
            }
        }
    }

    private fun isChangeNotAllowed() = when (state.value) {
        is AuthState.SubmitInProgress -> true
        else -> false
    }

    private fun isSubmitNotAllowed() = when (state.value) {
        is AuthState.SubmitInProgress -> true
        else -> false
    }
}

sealed interface AuthEvent {
    data class ChangeApiKey(val apiKey: String) : AuthEvent

    object Submit : AuthEvent

    data class ErrorReport(val info: ErrorReportInfo) : AuthEvent
}

sealed interface AuthState {
    val apiKey: String

    object Initial : AuthState {
        override val apiKey: String = ""
    }

    data class ApiKeyChanged(
        override val apiKey: String
    ) : AuthState

    data class SubmitInProgress(
        override val apiKey: String
    ) : AuthState

    data class Submitted(
        override val apiKey: String
    ) : AuthState

    sealed interface SubmitFailed : AuthState {
        object EmptyApiKey : SubmitFailed {
            override val apiKey: String = ""
        }

        data class SaveError(
            override val apiKey: String,
            val error: ApiKeyRepository.SaveResult.Failed,
        ) : SubmitFailed
    }
}

sealed interface AuthEffect {
    object Submitted : AuthEffect
}
