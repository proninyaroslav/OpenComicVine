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

package org.proninyaroslav.opencomicvine.model.paging

import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.proninyaroslav.opencomicvine.types.ComicVineResponse

abstract class ComicVineSource<Value : Any, Error : ComicVineSource.Error>(
    private val endOfPaginationOffset: Int? = null,
) : PagingSource<Int, Value>() {

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
        const val DEFAULT_MINI_PAGE_SIZE = 10

        inline fun <reified E : Error> stateToError(state: LoadState): E? {
            return (state as LoadState.Error).error.let {
                if (it is E) it else null
            }
        }
    }

    protected abstract suspend fun fetch(offset: Int, limit: Int): FetchResult<Value>

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? =
        ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val offset = params.key?.let {
            if (it < 0) {
                0
            } else if (it > (endOfPaginationOffset ?: Int.MAX_VALUE)) {
                endOfPaginationOffset
            } else {
                it
            }
        } ?: 0
        val limit = params.loadSize

        if (endOfPaginationOffset != null && endOfPaginationOffset == 0) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        }

        return when (val res = fetch(offset = offset, limit = limit)) {
            is FetchResult.Success -> {
                val response = res.response
                val endOfPaginationReached =
                    endOfPaginationOffset?.let { offset + limit >= it } ?: false
                            || response.numberOfPageResults == 0
                            || response.results.isEmpty()
                LoadResult.Page(
                    data = response.results,
                    prevKey = if (offset == 0) null else offset - limit,
                    nextKey = if (endOfPaginationReached) null else offset + limit,
                )
            }
            is FetchResult.Failed -> LoadResult.Error(res.error)
            FetchResult.Empty -> LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        }
    }

    protected sealed interface FetchResult<out T> {
        object Empty : FetchResult<Nothing>
        data class Success<T>(val response: ComicVineResponse<List<T>>) : FetchResult<T>
        data class Failed(val error: Error) : FetchResult<Nothing>
    }

    abstract class Error : Throwable()
}
