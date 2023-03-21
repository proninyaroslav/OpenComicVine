package org.proninyaroslav.opencomicvine.model.paging.details.volume

import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.item.volume.VolumeItem
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