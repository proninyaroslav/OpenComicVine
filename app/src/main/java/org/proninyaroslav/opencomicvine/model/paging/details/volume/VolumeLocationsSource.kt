package org.proninyaroslav.opencomicvine.model.paging.details.volume

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.LocationsFilter
import org.proninyaroslav.opencomicvine.data.item.volume.VolumeLocationItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.LocationsRepository

@AssistedFactory
interface VolumeLocationsSourceFactory {
    fun create(locations: List<VolumeDetails.Location>): VolumeLocationsSource
}

class VolumeLocationsSource @AssistedInject constructor(
    @Assisted private val locations: List<VolumeDetails.Location>,
    private val locationsRepo: LocationsRepository,
    private val favoritesRepo: FavoritesRepository,
) : VolumeRelatedEntitySource<VolumeDetails.Location, VolumeLocationItem>(locations) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, VolumeDetails.Location>,
    ): FetchResult<VolumeLocationItem> {
        val res = locationsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(LocationsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val items = results.map {
                            VolumeLocationItem(
                                info = it,
                                countOfAppearances = entitiesMap[it.id]!!.countOfAppearances,
                                isFavorite = favoritesRepo.observe(
                                    entityId = it.id,
                                    entityType = FavoriteInfo.EntityType.Location,
                                )
                            )
                        }
                        FetchResult.Success(copyResults(items))
                    }
                    else -> FetchResult.Failed(
                        DetailsEntitySource.Error.Service(
                            statusCode = statusCode,
                            errorMessage = error,
                        )
                    )
                }
            }
            else -> FetchResult.Failed(
                DetailsEntitySource.Error.Fetching(
                    error = res as ComicVineResult.Failed
                )
            )
        }
    }

    override fun VolumeDetails.Location.getId(): Int = id
}