package org.proninyaroslav.opencomicvine.ui.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiKeyRepo: ApiKeyRepository,
    private val pref: AppPreferences,
    private val errorReportService: ErrorReportService,
) : StoreViewModel<SettingsEvent, SettingsState, SettingsEffect>(SettingsState.Initial) {

    override val state: StateFlow<SettingsState> = merge(
        combine(
            apiKeyRepo.get(),
            pref.searchHistorySize,
            pref.theme,
        ) { apiKeyRes, searchHistorySize, theme ->
            SettingsState.Loaded(
                apiKey = when (apiKeyRes) {
                    is ApiKeyRepository.GetResult.Success -> apiKeyRes.data
                    else -> null
                },
                searchHistorySize = searchHistorySize,
                theme = theme,
            )
        },
        super.state,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState.Initial,
    )

    init {
        on<SettingsEvent.ChangeApiKey> { event ->
            viewModelScope.launch {
                changeApiKey(event)
            }
        }

        on<SettingsEvent.ChangeSearchHistorySize> { event ->
            viewModelScope.launch {
                pref.setSearchHistorySize(event.size)
                emitEffect(SettingsEffect.SearchHistorySizeChanged)
            }
        }

        on<SettingsEvent.ChangeTheme> { event ->
            viewModelScope.launch {
                pref.setTheme(event.theme)
                emitEffect(SettingsEffect.ThemeChanged)
            }
        }

        on<SettingsEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private suspend fun changeApiKey(event: SettingsEvent.ChangeApiKey) {
        val currentState = state.value
        val apiKey = event.apiKey.trim()
        if (apiKey.isBlank()) {
            emitState(
                SettingsState.ChangeApiKeyFailed.EmptyKey(
                    apiKey = apiKey,
                    searchHistorySize = currentState.searchHistorySize,
                    theme = currentState.theme,
                )
            )
            return
        }
        when (val res = apiKeyRepo.set(apiKey)) {
            is ApiKeyRepository.SaveResult.Success -> {
                emitEffect(SettingsEffect.ApiKeyChanged)
            }
            is ApiKeyRepository.SaveResult.Failed.IO -> {
                emitEffect(SettingsEffect.ChangeApiKeyFailed.SaveError(res))
            }
        }
    }
}

sealed interface SettingsEvent {
    data class ChangeApiKey(val apiKey: String) : SettingsEvent

    data class ChangeSearchHistorySize(val size: Int) : SettingsEvent {
        init {
            check(size >= 0)
        }
    }

    data class ChangeTheme(val theme: PrefTheme) : SettingsEvent

    data class ErrorReport(val info: ErrorReportInfo) : SettingsEvent
}

sealed interface SettingsState {
    val apiKey: String?
    val searchHistorySize: Int
    val theme: PrefTheme

    object Initial : SettingsState {
        override val apiKey: String? = null
        override val searchHistorySize: Int = 0
        override val theme: PrefTheme = PrefTheme.Unknown
    }

    data class Loaded(
        override val apiKey: String?,
        override val searchHistorySize: Int,
        override val theme: PrefTheme
    ) : SettingsState

    sealed interface ChangeApiKeyFailed : SettingsState {
        data class EmptyKey(
            override val apiKey: String?,
            override val searchHistorySize: Int,
            override val theme: PrefTheme
        ) : ChangeApiKeyFailed
    }
}

sealed interface SettingsEffect {
    object ApiKeyChanged : SettingsEffect

    object ThemeChanged : SettingsEffect

    object SearchHistorySizeChanged : SettingsEffect

    sealed interface ChangeApiKeyFailed : SettingsEffect {
        data class SaveError(val error: ApiKeyRepository.SaveResult.Failed) : ChangeApiKeyFailed
    }
}