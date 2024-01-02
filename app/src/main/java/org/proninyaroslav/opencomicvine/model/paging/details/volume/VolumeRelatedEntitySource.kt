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

package org.proninyaroslav.opencomicvine.model.paging.details.volume

import org.proninyaroslav.opencomicvine.types.copyResults
import org.proninyaroslav.opencomicvine.types.item.volume.VolumeItem
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.subListFrom

abstract class VolumeRelatedEntitySource<Entity : Any, Item : VolumeItem>(
    private val entities: List<Entity>
) : ComicVineSource<Item, DetailsEntitySource.Error>(
    endOfPaginationOffset = entities.size,
) {
    private val entitiesMap = entities.associateBy { it.getId() }
    private val idList = entitiesMap.keys.toList()

    protected abstract fun Entity.getId(): Int

    final override suspend fun fetch(offset: Int, limit: Int): FetchResult<Item> {
        return if (entities.isEmpty()) {
            FetchResult.Empty
        } else {
            return when (val res = fetch(
                offset = 0,
                limit = limit,
                idListRange = idList.subListFrom(offset = offset, maxLength = limit),
                entitiesMap = entitiesMap,
            )) {
                is FetchResult.Success -> res.response.run {
                    val items = results.sortedByDescending { it.countOfAppearances }
                    FetchResult.Success(copyResults(items))
                }
                FetchResult.Empty,
                is FetchResult.Failed -> res
            }
        }
    }

    protected abstract suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, Entity>
    ): FetchResult<Item>
}
