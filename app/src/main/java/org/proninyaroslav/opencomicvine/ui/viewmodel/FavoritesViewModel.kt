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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepo: FavoritesRepository,
    private val dateProvider: DateProvider,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : StoreViewModel<FavoritesEvent, FavoritesState, FavoritesEffect>(
    initialState = FavoritesState.Initial
) {
    init {
        on<FavoritesEvent.SwitchFavorite> { event ->
            viewModelScope.launch(ioDispatcher) {
                switchFavorite(
                    entityId = event.entityId,
                    entityType = event.entityType,
                )
            }
        }
    }

    private suspend fun switchFavorite(
        entityId: Int,
        entityType: FavoriteInfo.EntityType,
    ) {
        val res = favoritesRepo.get(
            entityId = entityId,
            entityType = entityType,
        )
        when (res) {
            is FavoritesRepository.Result.Success -> {
                if (res.data != null) {
                    when (val deleteRes = favoritesRepo.delete(res.data)) {
                        is FavoritesRepository.Result.Failed -> {
                            emitState(
                                FavoritesState.SwitchFavoriteFailed(
                                    entityId = entityId,
                                    entityType = entityType,
                                    error = deleteRes,
                                )
                            )
                            emitEffect(
                                FavoritesEffect.SwitchFavoriteFailed(
                                    entityId = entityId,
                                    entityType = entityType,
                                    error = deleteRes,
                                )
                            )
                        }
                        is FavoritesRepository.Result.Success -> {
                            emitState(
                                FavoritesState.Removed(
                                    entityId = entityId,
                                    entityType = entityType,
                                )
                            )
                            emitEffect(
                                FavoritesEffect.Removed(
                                    entityId = entityId,
                                    entityType = entityType,
                                )
                            )
                        }
                    }
                } else {
                    val addRes = favoritesRepo.add(
                        FavoriteInfo(
                            entityId = entityId,
                            entityType = entityType,
                            dateAdded = dateProvider.now,
                        )
                    )
                    when (addRes) {
                        is FavoritesRepository.Result.Failed -> {
                            emitState(
                                FavoritesState.SwitchFavoriteFailed(
                                    entityId = entityId,
                                    entityType = entityType,
                                    error = addRes,
                                )
                            )
                            emitEffect(
                                FavoritesEffect.SwitchFavoriteFailed(
                                    entityId = entityId,
                                    entityType = entityType,
                                    error = addRes,
                                )
                            )
                        }
                        is FavoritesRepository.Result.Success -> {
                            emitState(
                                FavoritesState.Added(
                                    entityId = entityId,
                                    entityType = entityType,
                                )
                            )
                            emitEffect(
                                FavoritesEffect.Added(
                                    entityId = entityId,
                                    entityType = entityType,
                                )
                            )
                        }
                    }
                }
            }
            is FavoritesRepository.Result.Failed -> {
                emitState(
                    FavoritesState.SwitchFavoriteFailed(
                        entityId = entityId,
                        entityType = entityType,
                        error = res,
                    )
                )
                emitEffect(
                    FavoritesEffect.SwitchFavoriteFailed(
                        entityId = entityId,
                        entityType = entityType,
                        error = res,
                    )
                )
            }
        }
    }
}

sealed interface FavoritesEvent {
    data class SwitchFavorite(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType,
    ) : FavoritesEvent
}

sealed interface FavoritesState {
    object Initial : FavoritesState

    data class Added(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType
    ) : FavoritesState

    data class Removed(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType
    ) : FavoritesState

    data class SwitchFavoriteFailed(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType,
        val error: FavoritesRepository.Result.Failed,
    ) : FavoritesState
}

sealed interface FavoritesEffect {
    data class Added(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType
    ) : FavoritesEffect

    data class Removed(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType
    ) : FavoritesEffect

    data class SwitchFavoriteFailed(
        val entityId: Int,
        val entityType: FavoriteInfo.EntityType,
        val error: FavoritesRepository.Result.Failed
    ) : FavoritesEffect
}
