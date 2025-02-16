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

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.proninyaroslav.opencomicvine.types.SearchInfo
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.copyResults
import org.proninyaroslav.opencomicvine.types.item.SearchItem
import org.proninyaroslav.opencomicvine.types.preferences.toComicVineResourceType
import org.proninyaroslav.opencomicvine.types.toFavoritesType
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.SearchRepository

@AssistedFactory
interface SearchSourceFactory {
    fun create(queryState: Flow<SearchSource.Query>): SearchSource
}

class SearchSource @AssistedInject constructor(
    @Assisted private val queryState: Flow<Query>,
    private val searchRepo: SearchRepository,
    private val favoritesRepo: FavoritesRepository,
    private val pref: AppPreferences,
) : ComicVineSource<SearchItem, SearchSource.Error>() {

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<SearchItem> {
        return when (val queryState = queryState.first()) {
            Query.Empty -> FetchResult.Empty
            is Query.Value -> {
                val searchFilter = pref.searchFilter.first()
                val res = searchRepo.search(
                    offset = offset,
                    limit = limit,
                    query = queryState.query,
                    resources = searchFilter.resources.toComicVineResourceType(),
                )

                return when (res) {
                    is ComicVineResult.Success -> res.response.run {
                        when (statusCode) {
                            StatusCode.OK -> FetchResult.Success(
                                copyResults(
                                    results.mapIndexed { index, item ->
                                        item.toItem(position = offset + index)
                                    }
                                )
                            )
                            else -> FetchResult.Failed(
                                Error.Service(
                                    statusCode = statusCode,
                                    errorMessage = error,
                                )
                            )
                        }
                    }
                    else -> FetchResult.Failed(
                        Error.Fetching(
                            error = res as ComicVineResult.Failed
                        )
                    )
                }
            }
        }
    }

    private fun SearchInfo.toItem(position: Int): SearchItem =
        SearchItem(
            id = position,
            info = this,
            isFavorite = toFavoritesType()?.let { entityType ->
                favoritesRepo.observe(
                    entityId = id,
                    entityType = entityType
                )
            } ?: flowOf(FavoriteFetchResult.Success(false))
        )

    sealed class Error : ComicVineSource.Error() {
        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()
    }

    sealed interface Query {
        data object Empty : Query

        data class Value(val query: String) : Query {
            init {
                check(query.isNotBlank()) { "Query value cannot be blank" }
            }
        }
    }
}
