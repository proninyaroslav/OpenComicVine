package org.proninyaroslav.opencomicvine.ui.auth

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class AuthGuardViewModel @Inject constructor(
    apiKeyRepo: ApiKeyRepository,
) : StoreViewModel<Unit, AuthGuardState, Unit>(AuthGuardState.Initial) {

    override val state: StateFlow<AuthGuardState> = apiKeyRepo.get()
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