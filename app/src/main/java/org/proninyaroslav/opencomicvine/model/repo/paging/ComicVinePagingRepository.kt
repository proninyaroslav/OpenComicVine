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

package org.proninyaroslav.opencomicvine.model.repo.paging

import androidx.paging.PagingSource
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys
import java.io.IOException

interface ComicVinePagingRepository<Item : ComicVinePagingItem, RemoteKey : ComicVineRemoteKeys> {
    suspend fun saveItems(
        items: List<Item>,
        remoteKeys: List<RemoteKey>,
        clearBeforeSave: Boolean,
    ): Result<Unit>

    suspend fun getRemoteKeysById(id: Int): Result<RemoteKey?>

    suspend fun getItemById(id: Int): Result<Item?>

    fun getAllSavedItems(): PagingSource<Int, Item>

    fun getSavedItems(count: Int): PagingSource<Int, Item>

    sealed interface Result<out T> {
        data class Success<T>(val value: T) : Result<T>

        sealed interface Failed : Result<Nothing> {
            data class IO(val exception: IOException) : Failed
        }
    }
}
