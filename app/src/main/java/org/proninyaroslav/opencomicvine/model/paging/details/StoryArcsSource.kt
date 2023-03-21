package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.StoryArcInfo
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.StoryArcsFilter
import org.proninyaroslav.opencomicvine.data.item.StoryArcItem
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.StoryArcsRepository

@AssistedFactory
interface StoryArcsSourceFactory {
    fun create(idList: List<Int>): StoryArcsSource
}

class StoryArcsSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    private val storyArcsRepo: StoryArcsRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<StoryArcItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<StoryArcItem> {
        val res = storyArcsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(StoryArcsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> FetchResult.Success(
                        copyResults(results.map { it.toItem() })
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

    private fun StoryArcInfo.toItem() =
        StoryArcItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.StoryArc,
            )
        )
}