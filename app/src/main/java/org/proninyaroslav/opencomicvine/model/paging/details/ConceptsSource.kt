package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.ConceptInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.ConceptsFilter
import org.proninyaroslav.opencomicvine.data.item.ConceptItem
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.ConceptsRepository
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

@AssistedFactory
interface ConceptsSourceFactory {
    fun create(idList: List<Int>): ConceptsSource
}

class ConceptsSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    private val conceptsRepo: ConceptsRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<ConceptItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<ConceptItem> {
        val res = conceptsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(ConceptsFilter.Id(idListRange)),
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

    private fun ConceptInfo.toItem() =
        ConceptItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Concept,
            )
        )
}