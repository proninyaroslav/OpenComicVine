package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.ObjectInfo
import org.proninyaroslav.opencomicvine.data.ObjectsResponse
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.data.sort.ObjectsSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface ObjectsRepository :
    ComicVineEntityRepository<ObjectInfo, Nothing, ObjectsSort, ObjectsFilter>

class ObjectsRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : ObjectsRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: ObjectsSort?,
        filters: List<ObjectsFilter>,
    ): ComicVineResult<ObjectsResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.objects(
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