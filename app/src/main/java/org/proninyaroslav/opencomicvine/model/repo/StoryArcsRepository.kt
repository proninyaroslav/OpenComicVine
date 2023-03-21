package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.StoryArcInfo
import org.proninyaroslav.opencomicvine.data.StoryArcsResponse
import org.proninyaroslav.opencomicvine.data.filter.StoryArcsFilter
import org.proninyaroslav.opencomicvine.data.sort.StoryArcsSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface StoryArcsRepository :
    ComicVineEntityRepository<StoryArcInfo, Nothing, StoryArcsSort, StoryArcsFilter>

class StoryArcsRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : StoryArcsRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: StoryArcsSort?,
        filters: List<StoryArcsFilter>,
    ): ComicVineResult<StoryArcsResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.storyArcs(
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