package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.TeamInfo
import org.proninyaroslav.opencomicvine.data.TeamsResponse
import org.proninyaroslav.opencomicvine.data.filter.TeamsFilter
import org.proninyaroslav.opencomicvine.data.sort.TeamsSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface TeamsRepository : ComicVineEntityRepository<TeamInfo, Nothing, TeamsSort, TeamsFilter>

class TeamsRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : TeamsRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: TeamsSort?,
        filters: List<TeamsFilter>,
    ): ComicVineResult<TeamsResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.teams(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            sort = sort,
            filter = filters.ifEmpty { null },
        )
        return response.toComicVineResult()
    }

    override suspend fun getItemDetailsById(id: Int): ComicVineResult<ComicVineResponse<Nothing>> {
        TODO("Not yet implemented")
    }
}