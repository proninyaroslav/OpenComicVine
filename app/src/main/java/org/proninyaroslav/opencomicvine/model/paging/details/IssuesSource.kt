package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.data.item.IssueItem
import org.proninyaroslav.opencomicvine.data.sort.IssuesSort
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository

@AssistedFactory
interface IssuesSourceFactory {
    fun create(
        idList: List<Int>,
        sort: Flow<IssuesSort?>? = null,
    ): IssuesSource
}

class IssuesSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    @Assisted private val sort: Flow<IssuesSort?>?,
    private val issuesRepo: IssuesRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<IssueItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<IssueItem> {
        val res = issuesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = sort?.first(),
            filters = listOf(IssuesFilter.Id(idListRange)),
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

    private fun IssueInfo.toItem() =
        IssueItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Issue,
            )
        )
}