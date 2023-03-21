package org.proninyaroslav.opencomicvine.model.paging.details.volume

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.PeopleFilter
import org.proninyaroslav.opencomicvine.data.item.volume.VolumePersonItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.PeopleRepository

@AssistedFactory
interface VolumePeopleSourceFactory {
    fun create(people: List<VolumeDetails.Person>): VolumePeopleSource
}

class VolumePeopleSource @AssistedInject constructor(
    @Assisted private val people: List<VolumeDetails.Person>,
    private val peopleRepo: PeopleRepository,
    private val favoritesRepo: FavoritesRepository,
) : VolumeRelatedEntitySource<VolumeDetails.Person, VolumePersonItem>(people) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, VolumeDetails.Person>,
    ): FetchResult<VolumePersonItem> {
        val res = peopleRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(PeopleFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val items = results.map {
                            VolumePersonItem(
                                info = it,
                                countOfAppearances = entitiesMap[it.id]!!.countOfAppearances,
                                isFavorite = favoritesRepo.observe(
                                    entityId = it.id,
                                    entityType = FavoriteInfo.EntityType.Person,
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

    override fun VolumeDetails.Person.getId(): Int = id
}