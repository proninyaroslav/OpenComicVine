package org.proninyaroslav.opencomicvine.model.paging.recent

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository

@AssistedFactory
interface IssuesRemoteMediatorFactory {
    fun create(endOfPaginationOffset: Int? = null): IssuesRemoteMediator
}

class IssuesRemoteMediator @AssistedInject constructor(
    @Assisted private val endOfPaginationOffset: Int?,
    private val issuesRepo: IssuesRepository,
    issuePagingRepo: PagingIssueRepository,
    private val pref: AppPreferences,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : RecentEntityRemoteMediator<PagingRecentIssueItem, IssueInfo, RecentIssueItemRemoteKeys>(
    pagingRepo = issuePagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<IssueInfo> {
        val res = issuesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = pref.recentIssuesSort.first().toComicVineSort(),
            filters = pref.recentIssuesFilters.first().toComicVineFiltersList(),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> FetchResult.Success(this)
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
        values: List<PagingRecentIssueItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<RecentIssueItemRemoteKeys> = values.map { value ->
        RecentIssueItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<IssueInfo>
    ): List<PagingRecentIssueItem> {
        var index = offset
        return fetchList.map { PagingRecentIssueItem(index = index++, info = it) }.toList()
    }
}