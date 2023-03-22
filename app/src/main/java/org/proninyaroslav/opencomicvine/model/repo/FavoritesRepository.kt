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

package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import java.io.IOException
import javax.inject.Inject

interface FavoritesRepository {
    fun get(
        entityId: Int,
        entityType: FavoriteInfo.EntityType,
    ): Result<FavoriteInfo?>

    fun observe(
        entityId: Int,
        entityType: FavoriteInfo.EntityType,
    ): Flow<FavoriteFetchResult>

    fun observeByType(
        entityType: FavoriteInfo.EntityType,
        sort: PrefFavoritesSort,
    ): Flow<FavoritesListFetchResult>

    suspend fun add(info: FavoriteInfo): Result<Unit>

    suspend fun delete(info: FavoriteInfo): Result<Unit>

    sealed interface Result<out T> {
        data class Success<T>(val data: T) : Result<T>

        sealed interface Failed : Result<Nothing> {
            data class IO(val exception: IOException) : Failed
        }
    }
}

sealed interface FavoriteFetchResult {
    data class Success(val isFavorite: Boolean) : FavoriteFetchResult

    sealed interface Failed : FavoriteFetchResult {
        data class IO(val exception: IOException) : Failed
    }
}

sealed interface FavoritesListFetchResult {
    data class Success(val entityList: List<FavoriteInfo>) : FavoritesListFetchResult

    sealed interface Failed : FavoritesListFetchResult {
        data class IO(val exception: IOException) : Failed
    }
}

class FavoritesRepositoryImpl @Inject constructor(
    appDatabase: AppDatabase,
) : FavoritesRepository {
    private val favoritesDao = appDatabase.favoritesDao()

    override fun get(
        entityId: Int,
        entityType: FavoriteInfo.EntityType,
    ): FavoritesRepository.Result<FavoriteInfo?> =
        try {
            FavoritesRepository.Result.Success(
                favoritesDao.get(
                    entityId = entityId,
                    entityType = entityType.name
                )
            )
        } catch (e: IOException) {
            FavoritesRepository.Result.Failed.IO(e)
        }

    override fun observe(
        entityId: Int,
        entityType: FavoriteInfo.EntityType,
    ): Flow<FavoriteFetchResult> =
        favoritesDao.observeDistinctUntilChanged(
            entityId = entityId,
            entityType = entityType.name
        )
            .map<FavoriteInfo?, FavoriteFetchResult> {
                FavoriteFetchResult.Success(isFavorite = it != null)
            }
            .catch {
                if (it is IOException) {
                    emit(FavoriteFetchResult.Failed.IO(it))
                } else {
                    throw it
                }
            }

    override fun observeByType(
        entityType: FavoriteInfo.EntityType,
        sort: PrefFavoritesSort,
    ): Flow<FavoritesListFetchResult> =
        favoritesDao.observeByTypeDistinctUntilChanged(
            entityType = entityType.name,
            isAsc = when (sort) {
                is PrefFavoritesSort.DateAdded -> when (sort.direction) {
                    PrefSortDirection.Unknown -> false
                    PrefSortDirection.Asc -> true
                    PrefSortDirection.Desc -> false
                }
                PrefFavoritesSort.Unknown -> false
            },
        )
            .map<List<FavoriteInfo>, FavoritesListFetchResult> { list ->
                FavoritesListFetchResult.Success(entityList = list)
            }
            .catch {
                if (it is IOException) {
                    emit(FavoritesListFetchResult.Failed.IO(it))
                } else {
                    throw it
                }
            }

    override suspend fun add(info: FavoriteInfo): FavoritesRepository.Result<Unit> =
        try {
            favoritesDao.insert(info)
            FavoritesRepository.Result.Success(Unit)
        } catch (e: IOException) {
            FavoritesRepository.Result.Failed.IO(e)
        }

    override suspend fun delete(info: FavoriteInfo): FavoritesRepository.Result<Unit> =
        try {
            favoritesDao.delete(info)
            FavoritesRepository.Result.Success(Unit)
        } catch (e: IOException) {
            FavoritesRepository.Result.Failed.IO(e)
        }
}
