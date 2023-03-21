package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.TeamInfo
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.TeamsFilter
import org.proninyaroslav.opencomicvine.data.item.TeamItem
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.TeamsRepository

@AssistedFactory
interface TeamsSourceFactory {
    fun create(idList: List<Int>): TeamsSource
}

class TeamsSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    private val teamsRepo: TeamsRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<TeamItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<TeamItem> {
        val res = teamsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(TeamsFilter.Id(idListRange)),
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

    private fun TeamInfo.toItem() =
        TeamItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Team,
            )
        )
}