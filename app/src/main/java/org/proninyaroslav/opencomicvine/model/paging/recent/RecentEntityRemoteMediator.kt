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

package org.proninyaroslav.opencomicvine.model.paging.recent

import kotlinx.coroutines.CoroutineDispatcher
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException

abstract class RecentEntityRemoteMediator<Value : ComicVinePagingItem, FetchValue : Any, RemoteKeys : ComicVineRemoteKeys>(
    private val pagingRepo: ComicVinePagingRepository<Value, RemoteKeys>,
    ioDispatcher: CoroutineDispatcher,
) : ComicVineRemoteMediator<
        Value,
        FetchValue,
        RemoteKeys,
        RecentEntityRemoteMediator.Error,
        >
    (ioDispatcher) {

    override suspend fun saveCache(
        values: List<Value>,
        keys: List<RemoteKeys>,
        clearCache: Boolean
    ): LocalResult<Unit> {
        return when (val res = pagingRepo.saveItems(
            items = values,
            remoteKeys = keys,
            clearBeforeSave = clearCache,
        )) {
            is ComicVinePagingRepository.Result.Success -> LocalResult.Success(Unit)
            is ComicVinePagingRepository.Result.Failed.IO -> {
                LocalResult.Failed(Error.Save.IO(res.exception))
            }
        }
    }

    override suspend fun getRemoteKeys(id: Int): LocalResult<RemoteKeys?> {
        return when (val res = pagingRepo.getRemoteKeysById(id)) {
            is ComicVinePagingRepository.Result.Success -> LocalResult.Success(res.value)
            is ComicVinePagingRepository.Result.Failed.IO -> {
                LocalResult.Failed(Error.Save.IO(res.exception))
            }
        }
    }

    sealed class Error : ComicVineRemoteMediator.Error() {
        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()

        sealed class Save : Error() {
            data class IO(val exception: IOException) : Save()
        }
    }
}
