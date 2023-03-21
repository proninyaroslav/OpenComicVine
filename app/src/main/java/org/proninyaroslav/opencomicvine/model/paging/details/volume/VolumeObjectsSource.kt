package org.proninyaroslav.opencomicvine.model.paging.details.volume

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.data.item.volume.VolumeObjectItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.ObjectsRepository

@AssistedFactory
interface VolumeObjectsSourceFactory {
    fun create(objects: List<VolumeDetails.Object>): VolumeObjectsSource
}

class VolumeObjectsSource @AssistedInject constructor(
    @Assisted private val objects: List<VolumeDetails.Object>,
    private val objectsRepo: ObjectsRepository,
    private val favoritesRepo: FavoritesRepository,
) : VolumeRelatedEntitySource<VolumeDetails.Object, VolumeObjectItem>(objects) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, VolumeDetails.Object>,
    ): FetchResult<VolumeObjectItem> {
        val res = objectsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(ObjectsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val items = results.map {
                            VolumeObjectItem(
                                info = it,
                                countOfAppearances = entitiesMap[it.id]!!.countOfAppearances,
                                isFavorite = favoritesRepo.observe(
                                    entityId = it.id,
                                    entityType = FavoriteInfo.EntityType.Object,
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

    override fun VolumeDetails.Object.getId(): Int = id
}