package org.proninyaroslav.opencomicvine.model.paging.favorites

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesIssueItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesIssueItem
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingIssueRepository

@AssistedFactory
interface IssuesRemoteMediatorFactory {
    fun create(
        scope: CoroutineScope,
        onRefresh: () -> Unit,
    ): IssuesRemoteMediator
}

@OptIn(FlowPreview::class)
class IssuesRemoteMediator @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val onRefresh: () -> Unit,
    private val issuesRepo: IssuesRepository,
    issuePagingRepo: PagingIssueRepository,
    private val pref: AppPreferences,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoritesEntityRemoteMediator<PagingFavoritesIssueItem, FavoritesIssueItem, FavoritesIssueItemRemoteKeys>(
    favoritesFlow = pref.favoriteIssuesSort.flatMapConcat { sort ->
        favoritesRepo.observeByType(
            entityType = FavoriteInfo.EntityType.Issue,
            sort = sort,
        )
    },
    pagingRepo = issuePagingRepo,
    scope = scope,
    onRefresh = onRefresh,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FavoritesIssueItem> {
        val res = issuesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(IssuesFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val map = favoritesItemMap.first()
                        val items = results.mapNotNull {
                            map[it.id]?.let { info ->
                                FavoritesIssueItem(
                                    info = it,
                                    dateAdded = info.dateAdded,
                                )
                            }
                        }.sort(pref.favoriteIssuesSort.first())
                        FetchResult.Success(copyResults(items))
                    }
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

    override fun buildRemoteKeys(
        values: List<PagingFavoritesIssueItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<FavoritesIssueItemRemoteKeys> = values.map { value ->
        FavoritesIssueItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<FavoritesIssueItem>,
    ): List<PagingFavoritesIssueItem> {
        var index = offset
        return fetchList.map {
            PagingFavoritesIssueItem(index = index++, item = it)
        }.toList()
    }
}