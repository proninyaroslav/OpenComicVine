package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.ConceptInfo
import org.proninyaroslav.opencomicvine.data.ConceptsResponse
import org.proninyaroslav.opencomicvine.data.filter.ConceptsFilter
import org.proninyaroslav.opencomicvine.data.sort.ConceptsSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface ConceptsRepository :
    ComicVineEntityRepository<ConceptInfo, Nothing, ConceptsSort, ConceptsFilter>

class ConceptsRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : ConceptsRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: ConceptsSort?,
        filters: List<ConceptsFilter>,
    ): ComicVineResult<ConceptsResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.concepts(
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