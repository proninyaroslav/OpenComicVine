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

package org.proninyaroslav.opencomicvine.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiKeyRepo: ApiKeyRepository,
    private val pref: AppPreferences,
    private val errorReportService: ErrorReportService,
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState.Initial,
    )

    val changeApiKey = ChangeApiKey()

    fun changeSearchHistorySize(size: Int) {
        check(size >= 0)
        viewModelScope.launch {
            pref.setSearchHistorySize(size)
        }
    }

    fun changeTheme(theme: PrefTheme) {
        viewModelScope.launch {
            pref.setTheme(theme)
        }
    }

    fun errorReport(info: ErrorReportInfo) = errorReportService.report(info)

    inner class ChangeApiKey {
        private val _state = MutableStateFlow<ChangeApiKeyState>(ChangeApiKeyState.Initial)
        val state: StateFlow<ChangeApiKeyState> = _state

        operator fun invoke(apiKey: String) {
            viewModelScope.launch {
                val apiKeyTrim = apiKey.trim()
                if (apiKeyTrim.isBlank()) {
                    _state.value = ChangeApiKeyState.Failed.EmptyKey
                    return@launch
                }
                _state.value = when (val res = apiKeyRepo.set(apiKeyTrim)) {
                    is ApiKeyRepository.SaveResult.Success -> ChangeApiKeyState.Success(apiKeyTrim)
                    is ApiKeyRepository.SaveResult.Failed.IO -> ChangeApiKeyState.Failed.SaveError(
                        res
                    )
                }
            }
        }
    }
}

sealed interface SettingsState {
    val apiKey: String?
    val searchHistorySize: Int
    val theme: PrefTheme

    data object Initial : SettingsState {
        override val apiKey: String? = null
        override val searchHistorySize: Int = 0
        override val theme: PrefTheme = PrefTheme.Unknown
    }

    data class Loaded(
        override val apiKey: String?,
        override val searchHistorySize: Int,
        override val theme: PrefTheme
    ) : SettingsState
}

sealed interface ChangeApiKeyState {
    data object Initial : ChangeApiKeyState

    data class Success(val apiKey: String) : ChangeApiKeyState

    sealed interface Failed : ChangeApiKeyState {
        data object EmptyKey : Failed

        data class SaveError(val error: ApiKeyRepository.SaveResult.Failed) : Failed
    }
}