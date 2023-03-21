package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.ObjectInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.data.item.ObjectItem
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.ObjectsRepository

@AssistedFactory
interface ObjectsSourceFactory {
    fun create(idList: List<Int>): ObjectsSource
}

class ObjectsSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    private val objectsRepo: ObjectsRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<ObjectItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<ObjectItem> {
        val res = objectsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(ObjectsFilter.Id(idListRange)),
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

    private fun ObjectInfo.toItem() =
        ObjectItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Object,
            )
        )
}