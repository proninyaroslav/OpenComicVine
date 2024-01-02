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

package org.proninyaroslav.opencomicvine.model.paging.details

import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.subListFrom

abstract class DetailsEntitySource<Value : Any>(
    private val idList: List<Int>
) : ComicVineSource<Value, DetailsEntitySource.Error>(
    endOfPaginationOffset = idList.size,
) {
    final override suspend fun fetch(offset: Int, limit: Int): FetchResult<Value> {
        return if (idList.isEmpty()) {
            FetchResult.Empty
        } else {
            fetch(
                offset = 0,
                limit = limit,
                idListRange = idList.subListFrom(offset = offset, maxLength = limit)
            )
        }
    }

    protected abstract suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
    ): FetchResult<Value>

    sealed class Error : ComicVineSource.Error() {
        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()
    }
}
