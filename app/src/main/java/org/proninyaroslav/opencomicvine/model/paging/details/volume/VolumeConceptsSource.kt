package org.proninyaroslav.opencomicvine.model.paging.details.volume

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.ConceptsFilter
import org.proninyaroslav.opencomicvine.data.item.volume.VolumeConceptItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.ConceptsRepository
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

@AssistedFactory
interface VolumeConceptsSourceFactory {
    fun create(concepts: List<VolumeDetails.Concept>): VolumeConceptsSource
}

class VolumeConceptsSource @AssistedInject constructor(
    @Assisted private val concepts: List<VolumeDetails.Concept>,
    private val conceptsRepo: ConceptsRepository,
    private val favoritesRepo: FavoritesRepository,
) : VolumeRelatedEntitySource<VolumeDetails.Concept, VolumeConceptItem>(concepts) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, VolumeDetails.Concept>,
    ): FetchResult<VolumeConceptItem> {
        val res = conceptsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(ConceptsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val items = results.map {
                            VolumeConceptItem(
                                info = it,
                                countOfAppearances = entitiesMap[it.id]!!.countOfAppearances,
                                isFavorite = favoritesRepo.observe(
                                    entityId = it.id,
                                    entityType = FavoriteInfo.EntityType.Concept,
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

    override fun VolumeDetails.Concept.getId(): Int = id
}