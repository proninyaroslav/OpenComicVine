package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.LocationInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.LocationsFilter
import org.proninyaroslav.opencomicvine.data.item.LocationItem
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.LocationsRepository

@AssistedFactory
interface LocationsSourceFactory {
    fun create(idList: List<Int>): LocationsSource
}

class LocationsSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    private val locationsRepo: LocationsRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<LocationItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<LocationItem> {
        val res = locationsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(LocationsFilter.Id(idListRange)),
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

    private fun LocationInfo.toItem() =
        LocationItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Location,
            )
        )
}