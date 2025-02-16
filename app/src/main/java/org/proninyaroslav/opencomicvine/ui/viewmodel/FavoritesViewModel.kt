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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepo: FavoritesRepository,
    private val dateProvider: DateProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val switchFavorite = SwitchFavorite()

    inner class SwitchFavorite {
        private val _state = MutableStateFlow<SwitchFavoriteState>(SwitchFavoriteState.Initial)
        val state: StateFlow<SwitchFavoriteState> = _state

        operator fun invoke(
            entityId: Int,
            entityType: FavoriteInfo.EntityType,
        ) = viewModelScope.launch(ioDispatcher) {
            val res = favoritesRepo.get(
                entityId = entityId,
                entityType = entityType,
            )
            when (res) {
                is FavoritesRepository.Result.Success -> {
                    if (res.data != null) {
                        _state.value = when (val deleteRes = favoritesRepo.delete(res.data)) {
                            is FavoritesRepository.Result.Failed -> SwitchFavoriteState.Failed(
                                deleteRes
                            )

                            is FavoritesRepository.Result.Success -> SwitchFavoriteState.Removed(
                                entityId, entityType
                            )
                        }
                    } else {
                        val addRes = favoritesRepo.add(
                            FavoriteInfo(
                                entityId = entityId,
                                entityType = entityType,
                                dateAdded = dateProvider.now,
                            )
                        )
                        _state.value = when (addRes) {
                            is FavoritesRepository.Result.Failed -> SwitchFavoriteState.Failed(
                                addRes
                            )

                            is FavoritesRepository.Result.Success -> SwitchFavoriteState.Added(
                                entityId, entityType
                            )
                        }
                    }
                }

                is FavoritesRepository.Result.Failed -> {
                    _state.value = SwitchFavoriteState.Failed(res)
                }
            }
        }
    }
}

sealed interface SwitchFavoriteState {
    data object Initial : SwitchFavoriteState

    data class Added(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType,
    ) : SwitchFavoriteState

    data class Removed(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType,
    ) : SwitchFavoriteState

    data class Failed(
        val error: FavoritesRepository.Result.Failed
    ) : SwitchFavoriteState
}
