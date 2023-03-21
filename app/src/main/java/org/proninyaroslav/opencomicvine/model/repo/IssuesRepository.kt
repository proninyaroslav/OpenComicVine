package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.IssueDetails
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.IssueResponse
import org.proninyaroslav.opencomicvine.data.IssuesResponse
import org.proninyaroslav.opencomicvine.data.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.data.sort.IssuesSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface IssuesRepository :
    ComicVineEntityRepository<IssueInfo, IssueDetails, IssuesSort, IssuesFilter>

class IssuesRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : IssuesRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: IssuesSort?,
        filters: List<IssuesFilter>,
    ): ComicVineResult<IssuesResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.issues(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            sort = sort,
            filter = filters.ifEmpty { null },
        )
        return response.toComicVineResult()
    }

    override suspend fun getItemDetailsById(id: Int): ComicVineResult<IssueResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.issue(apiKey = apiKey, id = id)
        return response.toComicVineResult()
    }
}