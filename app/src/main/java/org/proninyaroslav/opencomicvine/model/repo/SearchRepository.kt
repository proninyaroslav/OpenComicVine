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

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.types.*
import org.proninyaroslav.opencomicvine.types.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.types.filter.StoryArcsFilter
import org.proninyaroslav.opencomicvine.di.ApplicationScope
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject
import kotlin.math.ceil

interface SearchRepository {
    suspend fun search(
        offset: Int,
        limit: Int,
        query: String,
        resources: Set<ComicVineSearchResourceType>,
    ): ComicVineResult<SearchResponse>
}

class SearchRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : SearchRepository {

    override suspend fun search(
        offset: Int,
        limit: Int,
        query: String,
        resources: Set<ComicVineSearchResourceType>,
    ): ComicVineResult<SearchResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }

        val result = fetch(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            query = query,
            resources = resources,
        )

        return handleResult(
            result = result,
            limit = limit,
            offset = offset,
        )
    }

    private suspend fun fetch(
        apiKey: String,
        offset: Int,
        limit: Int,
        query: String,
        resources: Set<ComicVineSearchResourceType>,
    ) = withContext(coroutineScope.coroutineContext) {
        val fetchStoryArcs = resources.run {
            isEmpty() || contains(ComicVineSearchResourceType.StoryArc)
        }
        val fetchObject = resources.run {
            isEmpty() || contains(ComicVineSearchResourceType.Object)
        }
        val objectAndStoryArcsOnly = resources.size == 2 && resources.all {
            it == ComicVineSearchResourceType.StoryArc || it == ComicVineSearchResourceType.Object
        }

        val (offsetPerRes, limitPerRes) = if (objectAndStoryArcsOnly) {
            divAndCeil(offset, 2f) to divAndCeil(limit, 2f)
        } else if (resources.isEmpty() || fetchObject && fetchStoryArcs) {
            divAndCeil(offset, 3f) to divAndCeil(limit, 3f)
        } else if (fetchObject || fetchStoryArcs) {
            divAndCeil(offset, 2f) to divAndCeil(limit, 2f)
        } else {
            offset to limit
        }

        awaitAll(
            async {
                if (objectAndStoryArcsOnly) {
                    null
                } else {
                    comicVineService.search(
                        apiKey = apiKey,
                        offset = offsetPerRes,
                        limit = limitPerRes,
                        query = query,
                        resources = resources.toList().let {
                            if (it.isEmpty()) {
                                null
                            } else {
                                ComicVineSearchResourceTypeList(it)
                            }
                        }
                    )
                }
            },
            async {
                if (fetchStoryArcs) {
                    comicVineService.searchStoryArcs(
                        apiKey = apiKey,
                        offset = offsetPerRes,
                        limit = limitPerRes,
                        filter = listOf(StoryArcsFilter.Name(query)),
                    )
                } else {
                    null
                }
            },
            async {
                if (fetchObject) {
                    comicVineService.searchObjects(
                        apiKey = apiKey,
                        offset = offsetPerRes,
                        limit = limitPerRes,
                        filter = listOf(ObjectsFilter.Name(query)),
                    )
                } else {
                    null
                }
            }
        ).filterNotNull().map { it.toComicVineResult() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleResult(
        result: List<ComicVineResult<ComicVineResponse<out List<SearchInfo>>>>,
        limit: Int,
        offset: Int
    ): ComicVineResult<SearchResponse> {
        val failed = result.find { it is ComicVineResult.Failed } as ComicVineResult.Failed?
        if (failed != null) {
            return failed
        }
        val items = mutableListOf<SearchInfo>()
        var totalNumberOfPageResults = 0
        var totalNumberOfTotalResults = 0

        val successResult = result.map { it as ComicVineResult.Success<SearchResponse> }
        for (res in successResult) {
            res.response.run {
                items += results
                totalNumberOfPageResults += numberOfPageResults
                totalNumberOfTotalResults += numberOfTotalResults
            }
        }

        return ComicVineResult.Success(
            successResult.first().response.copy(
                limit = limit,
                offset = offset,
                numberOfPageResults = totalNumberOfPageResults,
                numberOfTotalResults = totalNumberOfTotalResults,
                results = items,
            )
        )
    }

    private fun divAndCeil(value: Int, division: Float): Int = ceil(value / division).toInt()
}
