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

package org.proninyaroslav.opencomicvine.ui.search.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepo: SearchHistoryRepository,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val dateProvider: DateProvider,
    private val pref: AppPreferences,
) : ViewModel() {

    val searchHistoryList = searchHistoryRepo.observeAll()
        .onEach(::deleteOldItems)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchHistoryRepository.Result.Success(emptyList()),
        )

    val addToHistory = AddToHistory()

    val deleteFromHistory = DeleteFromHistory()

    private fun deleteOldItems(res: SearchHistoryRepository.Result<List<SearchHistoryInfo>>) {
        if (res !is SearchHistoryRepository.Result.Success) {
            return
        }
        val historyList = res.data
        viewModelScope.launch(ioDispatcher) {
            val maxHistorySize = pref.searchHistorySize.first()
            if (historyList.size > maxHistorySize) {
                searchHistoryRepo.deleteList(
                    historyList.subList(maxHistorySize, historyList.size)
                )
            }
        }
    }

    inner class AddToHistory {
        private val _state = MutableStateFlow<AddToHistoryState>(AddToHistoryState.Initial)
        val state: StateFlow<AddToHistoryState> = _state

        operator fun invoke(query: String) {
            val info = SearchHistoryInfo(
                query = query,
                date = dateProvider.now,
            )
            viewModelScope.launch(ioDispatcher) {
                _state.value = when (val res = searchHistoryRepo.insert(info)) {
                    is SearchHistoryRepository.Result.Success -> AddToHistoryState.Success
                    is SearchHistoryRepository.Result.Failed.IO -> AddToHistoryState.Failed(res)
                }
            }
        }
    }

    inner class DeleteFromHistory {
        private val _state = MutableStateFlow<DeleteFromHistoryState>(
            DeleteFromHistoryState.Initial
        )
        val state: StateFlow<DeleteFromHistoryState> = _state

        operator fun invoke(info: SearchHistoryInfo) {
            viewModelScope.launch(ioDispatcher) {
                _state.value = when (val res = searchHistoryRepo.delete(info)) {
                    is SearchHistoryRepository.Result.Success -> DeleteFromHistoryState.Success
                    is SearchHistoryRepository.Result.Failed.IO -> DeleteFromHistoryState.Failed(res)
                }
            }
        }
    }
}

sealed interface AddToHistoryState {
    object Initial : AddToHistoryState

    object Success : AddToHistoryState

    data class Failed(
        val error: SearchHistoryRepository.Result.Failed
    ) : AddToHistoryState
}

sealed interface DeleteFromHistoryState {
    object Initial : DeleteFromHistoryState

    object Success : DeleteFromHistoryState

    data class Failed(
        val error: SearchHistoryRepository.Result.Failed
    ) : DeleteFromHistoryState
}